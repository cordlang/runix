package webserver

import (
	"fmt"
	"net/http"
	"time"
)

// ConfigServer serves the configuration interface on port 2222.
type ConfigServer struct {
	Dir    string
	server *http.Server
}

// NewConfigServer creates a new configuration server.
func NewConfigServer(dir string) *ConfigServer {
	return &ConfigServer{Dir: dir}
}

// StartInBackground starts the server asynchronously.
func (cs *ConfigServer) StartInBackground() chan error {
	errChan := make(chan error, 1)
	go func() {
		fs := http.FileServer(http.Dir(cs.Dir))
		cs.server = &http.Server{
			Addr:    ":2222",
			Handler: fs,
		}
		fmt.Println("⚙️  Configuración disponible en http://localhost:2222")
		if err := cs.server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			errChan <- err
		}
	}()
	time.Sleep(100 * time.Millisecond)
	return errChan
}

// Stop stops the configuration server.
func (cs *ConfigServer) Stop() error {
	if cs.server != nil {
		return cs.server.Close()
	}
	return nil
}
