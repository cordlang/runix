package commands

import (
	"bufio"
	"flag"
	"fmt"
	"os"
	"strings"
	"time"

	"github.com/briandowns/spinner"
	"runix/go/openrouter"
)

// Execute parses CLI arguments and dispatches to subcommands.
func Execute() {
	if len(os.Args) < 2 {
		usage()
		return
	}

	switch os.Args[1] {
	case "chat":
		chatCmd(os.Args[2:])
	case "create":
		createCmd(os.Args[2:])
	case "demo":
		demoCmd(os.Args[2:])
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
	fmt.Println("  runix demo")
}

// chatCmd sends a single message to the OpenRouter API.
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
	animate("generando respuesta")
	reply, err := client.Chat(*model, *context, fs.Arg(0))
	if err != nil {
		fmt.Println("error:", err)
		return
	}
	chat("runix", reply)
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

// demoCmd provides an interactive chat loop with animated output.
func demoCmd(args []string) {
	fs := flag.NewFlagSet("demo", flag.ExitOnError)
	context := fs.String("context", "", "context prompt")
	model := fs.String("model", "mistralai/mistral-small-3.2-24b-instruct:free", "model")
	fs.Parse(args)

	apiKey := os.Getenv("OPENROUTER_API_KEY")
	if apiKey == "" {
		fmt.Println("OPENROUTER_API_KEY environment variable not set")
		return
	}

	client := openrouter.NewClient(apiKey)
	scanner := bufio.NewScanner(os.Stdin)
	fmt.Println("Escribe 'exit' para terminar la conversación.")
	for {
		fmt.Print(":user: ")
		if !scanner.Scan() {
			break
		}
		msg := strings.TrimSpace(scanner.Text())
		if msg == "" {
			continue
		}
		if strings.ToLower(msg) == "exit" {
			break
		}
		animate("generando respuesta")
		reply, err := client.Chat(*model, *context, msg)
		if err != nil {
			fmt.Println("error:", err)
			continue
		}
		chat("runix", reply)
	}
}

func chat(role, msg string) {
	fmt.Printf(":%s: %s\n", role, msg)
}

func animate(action string) {
	s := spinner.New(spinner.CharSets[14], 100*time.Millisecond)
	s.Suffix = " " + action
	s.Start()
	time.Sleep(1 * time.Second)
	s.Stop()
	fmt.Printf("............... %s\n", action)
}
