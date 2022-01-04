.DEFAULT_GOAL := all
.PHONY: all clean dir

SRC_DIR := src
OUT_DIR := target

CLIENT_SRC := $(SRC_DIR)/client
SERVER_SRC := $(SRC_DIR)/server

CLIENT_FILES := $(shell find $(CLIENT_SRC) -name *.java)
SERVER_FILES := $(shell find $(SERVER_SRC) -name *.java)

CLIENT_JAR := $(OUT_DIR)/Client.jar
SERVER_JAR := $(OUT_DIR)/Server.jar

APP_DATA_DIR := $${HOME}/Documents/SD-TP-app-data



all:
	@make dir
	@make $(SERVER_JAR)
	@make $(CLIENT_JAR)

dir:
	@mkdir -p $(APP_DATA_DIR)

$(SERVER_JAR): $(SERVER_FILES)
	@cat build_server > pom.xml
	@mvn package

$(CLIENT_JAR): $(CLIENT_FILES)
	@cat build_client > pom.xml
	@mvn package



clean:
	@mvn clean