.DEFAULT_GOAL := all
.PHONY: install clean dir

SRC_DIR := src
OUT_DIR := target

SRC_FILES := $(shell find $(SRC_DIR) -name *.java)

CLIENT_JAR := $(OUT_DIR)/Client.jar
SERVER_JAR := $(OUT_DIR)/Server.jar

APP_DATA_DIR := $${HOME}/Documents/SD-TP-app-data



all:
	@make $(SERVER_JAR)
	@make $(CLIENT_JAR)
	@cp $(SERVER_JAR) $${HOME}/Documents
	@cp $(CLIENT_JAR) $${HOME}/Documents

install:
	@mkdir -p $(APP_DATA_DIR)

$(SERVER_JAR): $(SRC_FILES)
	@cat build_server > pom.xml
	@mvn package

$(CLIENT_JAR): $(SRC_FILES)
	@cat build_client > pom.xml
	@mvn package



clean:
	@mvn clean
