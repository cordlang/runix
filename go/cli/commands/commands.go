package commands

import (
	"bufio"
	"flag"
	"fmt"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"

	"github.com/briandowns/spinner"
	"runix/go/filemanager"
	"runix/go/openrouter"
	"runix/go/processor"
	"runix/go/webserver"
)

var (
	globalFileManager *filemanager.FileManager
	globalWebServer   *webserver.Server
	globalProcessor   *processor.ResponseProcessor
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
	case "server":
		serverCmd(os.Args[2:])
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
	fmt.Println("  runix server")
}

// initializeSystem initializes the file manager, web server, and processor
func initializeSystem() error {
	var err error

	// Initialize file manager
	globalFileManager, err = filemanager.NewFileManager()
	if err != nil {
		return fmt.Errorf("error inicializando gestor de archivos: %v", err)
	}

	// Initialize web server
	globalWebServer = webserver.NewServer(globalFileManager.GetProjectPath())

	// Initialize response processor
	globalProcessor = processor.NewResponseProcessor(globalFileManager, globalWebServer)

	fmt.Println("🚀 Sistema Runix inicializado")
	fmt.Printf("📁 Directorio de proyecto: %s\n", globalFileManager.GetProjectPath())

	return nil
}

// setupCleanup sets up cleanup handlers for graceful shutdown
func setupCleanup() {
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt, syscall.SIGTERM)

	go func() {
		<-c
		fmt.Println("\n🧹 Limpiando...")
		if globalWebServer != nil {
			globalWebServer.Stop()
		}
		if globalFileManager != nil {
			globalFileManager.Cleanup()
		}
		os.Exit(0)
	}()
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

	// Initialize system
	if err := initializeSystem(); err != nil {
		fmt.Printf("Error: %v\n", err)
		return
	}
	defer globalFileManager.Cleanup()

	apiKey := os.Getenv("OPENROUTER_API_KEY")
	if apiKey == "" {
		fmt.Println("OPENROUTER_API_KEY environment variable not set")
		return
	}

	client := openrouter.NewClient(apiKey)
	animate("generando respuesta")
	stream, err := client.ChatStream(*model, *context, fs.Arg(0))
	if err != nil {
		fmt.Println("error:", err)
		return
	}

	var fullReply strings.Builder
	for token := range stream {
		fmt.Print(token)
		fullReply.WriteString(token)
	}
	fmt.Println()

	processedReply := globalProcessor.ProcessResponse(fullReply.String())
	if processedReply != fullReply.String() {
		chat("runix", processedReply)
	}
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

	// Initialize system
	if err := initializeSystem(); err != nil {
		fmt.Printf("Error: %v\n", err)
		return
	}

	// Setup cleanup
	setupCleanup()

	apiKey := os.Getenv("OPENROUTER_API_KEY")
	if apiKey == "" {
		fmt.Println("OPENROUTER_API_KEY environment variable not set")
		return
	}

	client := openrouter.NewClient(apiKey)
	scanner := bufio.NewScanner(os.Stdin)
	fmt.Println("Escribe 'exit' para terminar la conversación.")
	fmt.Println("💡 Tip: Pide que cree HTML, CSS o JavaScript y se desplegará automáticamente en http://localhost:1111")

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
		stream, err := client.ChatStream(*model, *context, msg)
		if err != nil {
			fmt.Println("error:", err)
			continue
		}

		var fullReply strings.Builder
		for token := range stream {
			fmt.Print(token)
			fullReply.WriteString(token)
		}
		fmt.Println()

		processedReply := globalProcessor.ProcessResponse(fullReply.String())
		if processedReply != fullReply.String() {
			chat("runix", processedReply)
		}
	}

	// Cleanup
	if globalWebServer != nil {
		globalWebServer.Stop()
	}
	if globalFileManager != nil {
		globalFileManager.Cleanup()
	}
}

// serverCmd starts only the web server for existing project
func serverCmd(args []string) {
	// Initialize system
	if err := initializeSystem(); err != nil {
		fmt.Printf("Error: %v\n", err)
		return
	}

	// Setup cleanup
	setupCleanup()

	// Start server
	if err := globalWebServer.Start(); err != nil {
		fmt.Printf("Error iniciando servidor: %v\n", err)
	}
}

func chat(role, msg string) {
	// Format the output nicely
	fmt.Printf("\n:%s:\n%s\n\n", role, msg)
}

func animate(action string) {
	s := spinner.New(spinner.CharSets[14], 100*time.Millisecond)
	s.Suffix = " " + action
	s.Start()
	time.Sleep(1 * time.Second)
	s.Stop()
	fmt.Printf("............... %s\n", action)
}
