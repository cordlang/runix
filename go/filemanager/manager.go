package filemanager

import (
	"fmt"
	"os"
	"path/filepath"
	"regexp"
	"strings"
)

// FileManager manages temporary project files.
type FileManager struct {
	TempDir    string
	ProjectDir string
}

// NewFileManager creates temporary directories for a project.
func NewFileManager() (*FileManager, error) {
	tempDir := filepath.Join(os.TempDir(), "runix_temp")
	projectDir := filepath.Join(tempDir, "project")

	if err := os.MkdirAll(projectDir, 0o755); err != nil {
		return nil, err
	}

	return &FileManager{
		TempDir:    tempDir,
		ProjectDir: projectDir,
	}, nil
}

// ProcessAIResponse processes the AI response with debug output.
func (fm *FileManager) ProcessAIResponse(response string) error {
	return fm.processAIResponse(response, true)
}

// ProcessAIResponseQuiet processes the AI response without debug output.
func (fm *FileManager) ProcessAIResponseQuiet(response string) error {
	return fm.processAIResponse(response, false)
}

func (fm *FileManager) processAIResponse(response string, debug bool) error {
	if debug {
		fmt.Printf("DEBUG: Procesando respuesta de %d caracteres\n", len(response))
	}

	htmlBlocks := fm.extractCodeBlocks(response, debug, "html")
	if debug {
		fmt.Printf("DEBUG: Encontrados %d bloques HTML\n", len(htmlBlocks))
	}
	if len(htmlBlocks) > 0 {
		indexPath := filepath.Join(fm.ProjectDir, "index.html")
		if debug {
			fmt.Printf("DEBUG: Creando archivo HTML en: %s\n", indexPath)
		}
		return fm.writeFile(indexPath, htmlBlocks[0], debug)
	}

	cssBlocks := fm.extractCodeBlocks(response, debug, "css")
	if len(cssBlocks) > 0 {
		cssPath := filepath.Join(fm.ProjectDir, "style.css")
		return fm.writeFile(cssPath, cssBlocks[0], debug)
	}

	jsBlocks := fm.extractCodeBlocks(response, debug, "javascript", "js")
	if len(jsBlocks) > 0 {
		jsPath := filepath.Join(fm.ProjectDir, "script.js")
		return fm.writeFile(jsPath, jsBlocks[0], debug)
	}

	if debug {
		fmt.Printf("DEBUG: No se encontraron bloques de código\n")
	}
	return nil
}

// extractCodeBlocks extracts fenced code blocks from text.
func (fm *FileManager) extractCodeBlocks(text string, debug bool, languages ...string) []string {
	var blocks []string
	for _, lang := range languages {
		pattern := fmt.Sprintf("```%s(?:\\s*\\r?\\n)([\\s\\S]*?)\\r?\\n```", lang)
		re := regexp.MustCompile(pattern)
		matches := re.FindAllStringSubmatch(text, -1)
		if debug {
			fmt.Printf("DEBUG: Buscando patrón para %s: %s\n", lang, pattern)
			fmt.Printf("DEBUG: Encontradas %d coincidencias para %s\n", len(matches), lang)
		}
		for i, match := range matches {
			if len(match) > 1 {
				content := strings.TrimSpace(match[1])
				if debug {
					fmt.Printf("DEBUG: Bloque %d de %s (%d caracteres): %.100s...\n", i+1, lang, len(content), content)
				}
				blocks = append(blocks, content)
			}
		}
	}
	return blocks
}

// writeFile writes content to a file.
func (fm *FileManager) writeFile(path, content string, debug bool) error {
	if debug {
		fmt.Printf("📄 Creando archivo: %s\n", filepath.Base(path))
		fmt.Printf("DEBUG: Contenido del archivo (%d caracteres): %.200s...\n", len(content), content)
	}
	return os.WriteFile(path, []byte(content), 0o644)
}

// GetProjectPath returns the path to the generated project.
func (fm *FileManager) GetProjectPath() string {
	return fm.ProjectDir
}

// Cleanup removes the temporary directory.
func (fm *FileManager) Cleanup() error {
	return os.RemoveAll(fm.TempDir)
}
