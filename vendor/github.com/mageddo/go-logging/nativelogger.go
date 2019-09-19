package logging

import (
	"bytes"
	"context"
	"github.com/mageddo/go-logging/pkg/trace"
	"fmt"
	"runtime/debug"
	"strconv"
)

type defaultLogger struct {
	writer          Printer
	callerBackLevel int
	logLevel        int
}

func New(p Printer, level ...int) *defaultLogger {
	if len(level) > 0 {
		return &defaultLogger{p, level[0], DEBUG}
	}
	return &defaultLogger{p, 3, DEBUG}
}

func (l *defaultLogger) Debug(args ...interface{}) {
	l.print(args, DEBUG)
}

func (l *defaultLogger) Debugf(format string, args ...interface{}) {
	l.fPrint(format, args, DEBUG)
}

func (l *defaultLogger) Info(args ...interface{}) {
	l.print(args, INFO)
}
func (l *defaultLogger) Infof(format string, args ...interface{}) {
	l.fPrint(format, args, INFO)
}

func (l *defaultLogger) Warning(args ...interface{}) {
	l.print(args, WARNING)
}

func (l *defaultLogger) Warningf(format string, args ...interface{}) {
	l.fPrint(format, args, WARNING)
}

func (l *defaultLogger) Error(args ...interface{}) {
	l.print(args, ERROR)
}
func (l *defaultLogger) Errorf(format string, args ...interface{}) {
	l.fPrint(format, args, ERROR)
}

func (l *defaultLogger) Printer() Printer {
	return l.writer
}

func (l *defaultLogger) SetLevel(level int) {
	l.logLevel = level
}

func (l *defaultLogger) GetLevel() int {
	return l.logLevel
}

func transformErrorInStackTrace(args []interface{}, buf *bytes.Buffer) []interface{} {
	size := len(args)
	if size > 0 {
		last, ok := args[size - 1].(error)
		if ok {
			stack := fmt.Sprintf("\n%s\n%s", last.Error(), debug.Stack())
			if buf == nil {
				args[size - 1] = stack
			} else {
				buf.WriteString(stack)
				return args[:size-1]
			}
		}
	}
	return args
}

func (l *defaultLogger) fPrint(format string, args []interface{}, methodLevel int){
	args, ctx := popContext(args)
	vfmt := withFormat(withContextUUID(withCallerMethod(withLevel(new(bytes.Buffer), getLevelName(methodLevel)), l.callerBackLevel), ctx), format)
	args = transformErrorInStackTrace(args, vfmt)
	l.Printer().Printf(vfmt.String(), args...)
}

func (l *defaultLogger) print(args []interface{}, methodLevel int) {
	args = transformErrorInStackTrace(args, nil)
	args, ctx := popContext(args)
	args = append([]interface{}{withContextUUID(withCallerMethod(withLevel(new(bytes.Buffer), getLevelName(methodLevel)), l.callerBackLevel), ctx).String()}, args...)
	l.Printer().Println(args...)
}

func withContextUUID(buff *bytes.Buffer, ctx context.Context) *bytes.Buffer {
	if ctx == nil {
		return buff
	}
	buff.WriteString("uuid=")
	buff.WriteString(ctx.Value("UUID").(string))
	buff.WriteString(" ")
	return buff
}

// add method caller name to message
func withCallerMethod(buff *bytes.Buffer, level int) *bytes.Buffer {
	s := trace.GetCallerFunction(level)
	buff.WriteString("f=")
	buff.WriteString(s.FileName)
	buff.WriteString(":")
	buff.WriteString(strconv.Itoa(s.Line))
	buff.WriteString(" pkg=")
	buff.WriteString(s.PackageName)
	buff.WriteString(" m=")
	buff.WriteString(s.Funcname)
	buff.WriteString(" ")
	return buff
}

// adding level to message
func withLevel(buff *bytes.Buffer, lvl string) *bytes.Buffer {
	buff.WriteString(lvl)
	buff.WriteString(" ")
	return buff
}

// adding format string to message
func withFormat(buff *bytes.Buffer, format string) *bytes.Buffer {
	buff.WriteString(format)
	return buff
}

func popContext(args []interface{}) ([]interface{}, context.Context) {
	if len(args) == 0 {
		return args, nil
	}
	if ctx, ok := args[0].(context.Context); ok {
		return args[1:], ctx
	}
	return args, nil
}

func getLevelName(level int) string {
	switch level {
	case DEBUG:
		return "DEBUG"
	case INFO:
		return "INFO"
	case NOTICE:
		return "NOTICE"
	case WARNING:
		return "WARNING"
	case ERROR:
		return "ERROR"
	default:
		panic(fmt.Sprintf("unknown code %d", level))
	}
}
