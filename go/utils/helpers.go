package utils

import "fmt"

// Log prints a label and value to stdout.
func Log(label string, value interface{}) {
	fmt.Printf("%s: %v\n", label, value)
}
