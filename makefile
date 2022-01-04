.DEFAULT_GOAL := all
.PHONY: all clean

SRC_DIR := src
OUT_DIR := target

CLIENT_SRC := $(SRC_DIR)/client
SERVER_SRC := $(SRC_DIR)/server

CLIENT_FILES := $(shell find $(CLIENT_SRC) -name *.java)
SERVER_FILES := $(shell find $(SERVER_SRC) -name *.java)

CLIENT_JAR := $(OUT_DIR)/Client.jar
SERVER_JAR := $(OUT_DIR)/Server.jar



all:
	@make $(SERVER_JAR)
	@make $(CLIENT_JAR)

$(SERVER_JAR): $(SERVER_FILES)
	@cat build_server > pom.xml
	@mvn package

$(CLIENT_JAR): $(CLIENT_FILES)
	@cat build_client > pom.xml
	@mvn package



clean:
	@mvn clean