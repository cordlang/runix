package chat

import (
	"fmt"
	"regexp"
	"strings"
	"time"

	"github.com/briandowns/spinner"
	"github.com/fatih/color"
)

type ChatStreamer struct {
	typingSpeed time.Duration
}

// NewChatStreamer creates a new chat streamer
func NewChatStreamer() *ChatStreamer {
	return &ChatStreamer{
		typingSpeed: 50 * time.Millisecond, // Velocidad de escritura
	}
}

// StreamResponse streams the AI response in real-time with special handling for code blocks
func (cs *ChatStreamer) StreamResponse(role, response string) {
	// Colors for different roles
	userColor := color.New(color.FgCyan, color.Bold)
	botColor := color.New(color.FgGreen, color.Bold)

	// Print role header
	fmt.Print("\n")
	if role == "user" {
		userColor.Print("👤 Usuario: ")
	} else {
		botColor.Print("🤖 Runix: ")
	}
	fmt.Print("\n")

	// Check if response contains code blocks
	if cs.containsCodeBlocks(response) {
		cs.streamWithCodeProcessing(response)
	} else {
		cs.streamText(response)
	}

	fmt.Print("\n\n")
}

// streamWithCodeProcessing handles responses that contain code blocks
func (cs *ChatStreamer) streamWithCodeProcessing(response string) {
	// Split response into parts: before code, code blocks, after code
	parts := cs.splitResponseByCode(response)

	for i, part := range parts {
		if cs.isCodeBlock(part.content) {
			// Show loading for code processing
			cs.showCodeProcessing(part.language)
		} else {
			// Stream normal text
			cs.streamText(part.content)
		}

		// Small pause between parts
		if i < len(parts)-1 {
			time.Sleep(200 * time.Millisecond)
		}
	}
}

// streamText streams normal text word by word
func (cs *ChatStreamer) streamText(text string) {
	words := strings.Fields(text)

	for i, word := range words {
		fmt.Print(word)
		if i < len(words)-1 {
			fmt.Print(" ")
		}
		time.Sleep(cs.typingSpeed)
	}
}

// showCodeProcessing shows a loading animation for code processing
func (cs *ChatStreamer) showCodeProcessing(language string) {
	codeColor := color.New(color.FgYellow, color.Bold)

	fmt.Print("\n\n")
	codeColor.Printf("🔧 Detectando código %s...\n", strings.ToUpper(language))

	// Animated progress bar
	cs.showProgressBar("Procesando código", 20)

	codeColor.Print("📄 Creando archivos...\n")
	cs.showProgressBar("Guardando archivos", 15)

	codeColor.Print("🌐 Iniciando servidor web...\n")
	cs.showProgressBar("Configurando servidor", 10)

	successColor := color.New(color.FgGreen, color.Bold)
	successColor.Print("✅ ¡Proyecto desplegado exitosamente!\n")

	linkColor := color.New(color.FgBlue, color.Bold, color.Underline)
	fmt.Print("🌐 Tu proyecto está disponible en: ")
	linkColor.Print("http://localhost:1111")
	fmt.Print("\n")
}

// showProgressBar shows an animated progress bar
func (cs *ChatStreamer) showProgressBar(message string, steps int) {
	fmt.Printf("%s ", message)

	for i := 0; i <= steps; i++ {
		// Clear line and redraw progress
		fmt.Print("\r" + message + " ")

		// Draw progress bar
		progress := float64(i) / float64(steps)
		barLength := 20
		filled := int(progress * float64(barLength))

		fmt.Print("[")
		for j := 0; j < barLength; j++ {
			if j < filled {
				color.New(color.FgGreen).Print("█")
			} else {
				color.New(color.FgHiBlack).Print("░")
			}
		}
		fmt.Printf("] %d%%", int(progress*100))

		time.Sleep(100 * time.Millisecond)
	}
	fmt.Print("\n")
}

// ResponsePart represents a part of the response (text or code)
type ResponsePart struct {
	content  string
	isCode   bool
	language string
}

// splitResponseByCode splits response into text and code parts
func (cs *ChatStreamer) splitResponseByCode(response string) []ResponsePart {
	var parts []ResponsePart

	// Regex to find code blocks
	codeRegex := regexp.MustCompile("(?s)```(\\w+)?\\s*\\n(.*?)\\n```")

	lastEnd := 0
	matches := codeRegex.FindAllStringSubmatchIndex(response, -1)

	for _, match := range matches {
		// Add text before code block
		if match[0] > lastEnd {
			textContent := response[lastEnd:match[0]]
			if strings.TrimSpace(textContent) != "" {
				parts = append(parts, ResponsePart{
					content: strings.TrimSpace(textContent),
					isCode:  false,
				})
			}
		}

		// Add code block
		language := "code"
		if match[2] != -1 && match[3] != -1 {
			language = response[match[2]:match[3]]
		}

		parts = append(parts, ResponsePart{
			content:  response[match[0]:match[1]],
			isCode:   true,
			language: language,
		})

		lastEnd = match[1]
	}

	// Add remaining text after last code block
	if lastEnd < len(response) {
		textContent := response[lastEnd:]
		if strings.TrimSpace(textContent) != "" {
			parts = append(parts, ResponsePart{
				content: strings.TrimSpace(textContent),
				isCode:  false,
			})
		}
	}

	// If no code blocks found, return entire response as text
	if len(parts) == 0 {
		parts = append(parts, ResponsePart{
			content: response,
			isCode:  false,
		})
	}

	return parts
}

// isCodeBlock checks if content is a code block
func (cs *ChatStreamer) isCodeBlock(content string) bool {
	return strings.HasPrefix(content, "```")
}

// containsCodeBlocks checks if response contains code blocks
func (cs *ChatStreamer) containsCodeBlocks(response string) bool {
	patterns := []string{
		"```html",
		"```css",
		"```javascript",
		"```js",
		"```python",
		"```go",
	}

	for _, pattern := range patterns {
		if strings.Contains(response, pattern) {
			return true
		}
	}
	return false
}

// ShowWelcome shows the welcome message with style
func (cs *ChatStreamer) ShowWelcome() {
	titleColor := color.New(color.FgMagenta, color.Bold)
	subtitleColor := color.New(color.FgCyan)
	tipColor := color.New(color.FgYellow)

	fmt.Print("\n")
	titleColor.Print("🚀 ═══════════════════════════════════════\n")
	titleColor.Print("      RUNIX CHAT - MODO INTERACTIVO      \n")
	titleColor.Print("   ═══════════════════════════════════════\n")

	subtitleColor.Print("💬 Chatea con IA y crea proyectos web al instante\n")
	tipColor.Print("💡 Tip: Pide crear HTML, CSS o JavaScript y se desplegará en http://localhost:1111\n")

	color.New(color.FgHiBlack).Print("   Escribe 'exit' para salir\n")
	fmt.Print("\n")
}

// ShowUserPrompt shows the user input prompt
func (cs *ChatStreamer) ShowUserPrompt() {
	promptColor := color.New(color.FgCyan, color.Bold)
	promptColor.Print("👤 Tú: ")
}

// ShowThinking shows thinking animation
func (cs *ChatStreamer) ShowThinking() {
	thinkingColor := color.New(color.FgYellow)

	s := spinner.New(spinner.CharSets[14], 100*time.Millisecond)
	s.Suffix = " Runix está pensando..."
	s.Color("yellow")
	s.Start()
	time.Sleep(800 * time.Millisecond)
	s.Stop()

	thinkingColor.Print("💭 Generando respuesta...\n")
}
