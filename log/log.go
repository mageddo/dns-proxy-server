package log

import (
	"github.com/mageddo/dns-proxy-server/conf"
	"github.com/mageddo/go-logging"
	"github.com/mageddo/go-logging/native"
	"io"
	"log"
	"os"
)

func init(){
	setup(os.Stdout)
	SetOutput(conf.LogFile())
	level := conf.LogLevel()
	logging.Warningf("status=log-level-changed, log-level=%d", level)
	logging.SetLevel(level)
}

func setup(out io.Writer) {
	logging.SetLog(logging.New(native.NewGologPrinter(out, "", log.LstdFlags | log.Lmicroseconds), 4))
}

func SetOutput(f string) error {
	if f == "console" {
		setup(os.Stdout)
		return nil
	}

	out, err := os.OpenFile(f, os.O_CREATE|os.O_APPEND|os.O_WRONLY, os.ModeAppend)
	if err != nil {
		return err
	}
	setup(out)
	return nil
}
