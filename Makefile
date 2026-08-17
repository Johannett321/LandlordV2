.PHONY: help build dev up logs console stop clean reset

help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) \
		| awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2}'

build: ## Compile the plugin to target/Landlord.jar
	mvn -B clean package

dev: build up ## Build the plugin and start the dev server (default target)

up: ## Start the dev server without rebuilding
	docker compose up

logs: ## Follow the server log
	docker compose logs -f

console: ## Attach to the interactive server console (Ctrl-P Ctrl-Q to detach)
	docker attach landlord-dev

stop: ## Stop the dev server
	docker compose down

clean: ## Remove build output
	mvn -B clean

reset: stop ## Delete all server state (worlds, player data, plugin config)
	rm -rf run/data
