package vo

func CreateNetworkDisconnectVO(errs []error) []string {
	results := make([]string, len(errs))
	for _, err := range errs {
		results = append(results, err.Error())
	}
	return results
}
