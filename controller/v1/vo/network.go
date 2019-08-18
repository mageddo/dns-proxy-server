package vo

func CreateNetworkDisconnectVO(errs []error) []string {
	results := make([]string, len(errs))
	for i, err := range errs {
		results[i] = err.Error()
	}
	return results
}
