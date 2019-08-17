package utils

import (
	"os/exec"
	"syscall"
	"reflect"
	"github.com/mageddo/go-logging"
)

func Exec(cmd string, args ...string) ( out []byte, err error, exitCode int ){

	logging.Infof("cmd=%s, args=%v", cmd, args)

	execution := exec.Command(cmd, args...)
	// ja chama o run dentro dele
	out, err = execution.CombinedOutput()

	if err != nil {
		logging.Infof("status=error, type=%v, err=%v", reflect.TypeOf(err), err)
		if exitError, ok := err.(*exec.ExitError); ok {
			exitCode = exitError.Sys().(syscall.WaitStatus).ExitStatus()
			return
		} else {
			exitCode = -255
			return
		}
	} else {
		exitCode = execution.ProcessState.Sys().(syscall.WaitStatus).ExitStatus()
	}

	if exitCode != 0 {
		logging.Warningf("status=bad-exit-code, status=%d", exitCode)
		return
	}
	logging.Infof("status=success, cmd=%s", cmd)
	return
}

func Exists(cmd string) bool {
	_, _, i := Exec("sh", "-c", "command -v " + cmd + " || false")
	switch i {
	case 0:
		return true
	case -255:
		panic("Command checker not exists")
	default:
		return false

	}
}
