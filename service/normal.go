package service

import (
	"github.com/mageddo/dns-proxy-server/utils"
)

func NewNormalScript() (*Script) {
	return &Script{utils.SolveRelativePath("/dns-proxy-server")}
}
