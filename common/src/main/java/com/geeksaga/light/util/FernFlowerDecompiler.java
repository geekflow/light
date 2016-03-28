/*
 * Copyright 2015 GeekSaga.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.geeksaga.light.util;

import org.jetbrains.java.decompiler.main.extern.IBytecodeProvider;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.io.IOException;
import java.util.jar.Manifest;

/**
 * @author geeksaga
 */
class FernFlowerDecompiler implements IBytecodeProvider, IResultSaver {
    private byte[] source;
    private String content;

    FernFlowerDecompiler() {
    }

    void setByteCode(byte[] source) {
        this.source = source;
    }

    String getContent() {
        return content;
    }

    public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
        if (source != null) {
            return source;
        }

        return new byte[0];
    }

    public void saveFolder(String path) {
    }

    public void copyFile(String source, String path, String entryName) {
    }

    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
        this.content = content;
    }

    public void createArchive(String path, String archiveName, Manifest manifest) {
    }

    public void saveDirEntry(String path, String archiveName, String entryName) {
    }

    public void copyEntry(String source, String path, String archiveName, String entry) {
    }

    public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
    }

    public void closeArchive(String path, String archiveName) {
    }
}
