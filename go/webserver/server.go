package webserver

import (
	"fmt"
	"net/http"
	"os"
	"time"
)

type Server struct {
	ProjectDir string
	server     *http.Server
}

// NewServer creates a new web server
func NewServer(projectDir string) *Server {
	return &Server{
		ProjectDir: projectDir,
	}
}

// Start starts the web server on port 1111
func (s *Server) Start() error {
	fs := http.FileServer(http.Dir(s.ProjectDir))
	mux := http.NewServeMux()
	mux.Handle("/", fs)

	s.server = &http.Server{
		Addr:    ":1111",
		Handler: mux,
	}

	fmt.Println("🌐 Servidor iniciado en http://localhost:1111")
	fmt.Println("   Presiona Ctrl+C para detener el servidor")

	return s.server.ListenAndServe()
}

// StartInBackground starts the server in a goroutine
func (s *Server) StartInBackground() chan error {
	errChan := make(chan error, 1)

	go func() {
		fs := http.FileServer(http.Dir(s.ProjectDir))
		mux := http.NewServeMux()
		mux.Handle("/", fs)

		s.server = &http.Server{
			Addr:    ":1111",
			Handler: mux,
		}

		fmt.Println("🌐 Servidor iniciado en http://localhost:1111")

		if err := s.server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			errChan <- err
		}
	}()

	// Give the server a moment to start
	time.Sleep(100 * time.Millisecond)
	return errChan
}

// StartInBackgroundQuiet starts the server in a goroutine without output
func (s *Server) StartInBackgroundQuiet() chan error {
	errChan := make(chan error, 1)

	go func() {
		fs := http.FileServer(http.Dir(s.ProjectDir))
		mux := http.NewServeMux()
		mux.Handle("/", fs)

		s.server = &http.Server{
			Addr:    ":1111",
			Handler: mux,
		}

		if err := s.server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			errChan <- err
		}
	}()

	// Give the server a moment to start
	time.Sleep(100 * time.Millisecond)
	return errChan
}

// Stop gracefully stops the server
func (s *Server) Stop() error {
	if s.server != nil {
		return s.server.Close()
	}
	return nil
}

// CheckIfFileExists checks if index.html exists in project directory
func (s *Server) CheckIfFileExists() bool {
	indexPath := fmt.Sprintf("%s/index.html", s.ProjectDir)
	_, err := os.Stat(indexPath)
	return err == nil
}
