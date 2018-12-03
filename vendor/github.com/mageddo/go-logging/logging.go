package logging

//
// Who write the logs to output
//
import (
	"os"
	"log"
	"io"
	"github.com/mageddo/go-logging/native"
)

// in accord to https://tools.ietf.org/html/rfc5424
const (
	ERROR = 3
	WARNING = 4
	NOTICE = 5
	INFO = 6
	DEBUG = 7
)

type Printer interface {
	Printf(format string, args ...interface{})
	Println(args ...interface{})
	SetOutput(w io.Writer)
}

//
// The package logger interface, you can create as many impl as you want
//
type Log interface {

	Debug(args ...interface{})
	Debugf(format string, args ...interface{})

	Info(args ...interface{})
	Infof(format string, args ...interface{})

	Warning(args ...interface{})
	Warningf(format string, args ...interface{})

	Error(args ...interface{})
	Errorf(format string, args ...interface{})

	Printer() Printer

	// specify log level
	SetLevel(level int)
	GetLevel() int
}

var l Log = New(native.NewGologPrinter(os.Stdout, "", log.LstdFlags), 4)
func Debug(args ...interface{}) {
	if isActive(l.GetLevel(), DEBUG) {
		l.Debug(args...)
	}
}

func Debugf(format string, args ...interface{}){
	if isActive(l.GetLevel(), DEBUG) {
		l.Debugf(format, args...)
	}
}

func Info(args ...interface{}){
	if isActive(l.GetLevel(), INFO) {
		l.Info(args...)
	}
}

func Infof(format string, args ...interface{}){
	if isActive(l.GetLevel(), INFO) {
		l.Infof(format, args...)
	}
}

func Warning(args ...interface{}){
	if isActive(l.GetLevel(), WARNING) {
		l.Warning(args...)
	}
}

func Warningf(format string, args ...interface{}) {
	if isActive(l.GetLevel(), WARNING) {
		l.Warningf(format, args...)
	}
}

func Error(args ...interface{}){
	if isActive(l.GetLevel(), ERROR) {
		l.Error(args...)
	}
}

func Errorf(format string, args ...interface{}){
	if isActive(l.GetLevel(), ERROR) {
		l.Errorf(format, args...)
	}
}

func SetOutput(w io.Writer) {
	l.Printer().SetOutput(w)
}

//
// Change actual logger
//
func SetLog(logger Log){
	l = logger
}

//
// Returns current logs
//
func GetLog() Log {
	return l
}

func SetLevel(level int){
	l.SetLevel(level)
}

func GetLevel() int {
	return l.GetLevel()
}

func isActive(currentLevel, levelToCompare int) bool {
	return currentLevel >= levelToCompare
}
