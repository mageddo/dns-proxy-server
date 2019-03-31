package resolvconf

import "strings"

type dnsServerCleanerHandler struct {
	serverIP string
}

func (hd dnsServerCleanerHandler) process(line string, entryType DnsEntry) *string {

	switch entryType {
	case PROXY:
		return nil
	case COMMENTED_SERVER:
		v := line[2: strings.Index(line, " # dps-comment")]
		return &v
	case SERVER:
		return &line
	default:
		return &line
	}
}

func (hd dnsServerCleanerHandler) afterProcess(hasContent bool, foundDnsProxy bool) *string {
	return nil
}

func newDNSServerCleanerHandler() DnsHandler {
	hd := dnsServerCleanerHandler{}
	return &hd
}
