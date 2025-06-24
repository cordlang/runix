package main

import (
	"flag"
	"fmt"
	"os"

	"runix/go/openrouter"
)

func main() {
	if len(os.Args) < 2 {
		usage()
		return
	}

	switch os.Args[1] {
	case "chat":
		chatCmd(os.Args[2:])
	case "create":
		createCmd(os.Args[2:])
	default:
		fmt.Println("Unknown command:", os.Args[1])
		usage()
	}
}

func usage() {
	fmt.Println("Runix CLI")
	fmt.Println("Usage:")
	fmt.Println("  runix chat [options] <message>")
	fmt.Println("  runix create <directory>")
}

func chatCmd(args []string) {
	fs := flag.NewFlagSet("chat", flag.ExitOnError)
	context := fs.String("context", "", "context prompt")
	model := fs.String("model", "mistralai/mistral-small-3.2-24b-instruct:free", "model")
	fs.Parse(args)

	if fs.NArg() < 1 {
		fmt.Println("message is required")
		return
	}

	apiKey := os.Getenv("OPENROUTER_API_KEY")
	if apiKey == "" {
		fmt.Println("OPENROUTER_API_KEY environment variable not set")
		return
	}

	client := openrouter.NewClient(apiKey)
	reply, err := client.Chat(*model, *context, fs.Arg(0))
	if err != nil {
		fmt.Println("error:", err)
		return
	}
	fmt.Println(reply)
}

func createCmd(args []string) {
	if len(args) < 1 {
		fmt.Println("directory path is required")
		return
	}
	path := args[0]
	if err := os.MkdirAll(path, 0755); err != nil {
		fmt.Println("error creating directory:", err)
		return
	}
	fmt.Println("created directory", path)
}
