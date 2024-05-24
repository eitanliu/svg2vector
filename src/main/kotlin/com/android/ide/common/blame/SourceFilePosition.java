/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ide.common.blame;

import com.google.common.base.Objects;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class SourceFilePosition {

    public static final SourceFilePosition UNKNOWN =
            new SourceFilePosition(SourceFile.UNKNOWN, SourcePosition.UNKNOWN);

    @NotNull
    private final SourceFile mSourceFile;

    @NotNull
    private final SourcePosition mSourcePosition;

    public SourceFilePosition(@NotNull SourceFile sourceFile,
            @NotNull SourcePosition sourcePosition) {
        mSourceFile = sourceFile;
        mSourcePosition = sourcePosition;
    }

    public SourceFilePosition(@NotNull File file,
            @NotNull SourcePosition sourcePosition) {
        this(new SourceFile(file), sourcePosition);
    }

    @NotNull
    public SourcePosition getPosition() {
        return mSourcePosition;
    }

    @NotNull
    public SourceFile getFile() {
        return mSourceFile;
    }

    @NotNull
    @Override
    public String toString() {
        return print(false);
    }

    @NotNull
    public String print(boolean shortFormat) {
        if (mSourcePosition.equals(SourcePosition.UNKNOWN)) {
            return mSourceFile.print(shortFormat);
        } else {
            return mSourceFile.print(shortFormat) + ':' + mSourcePosition.toString();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mSourceFile, mSourcePosition);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SourceFilePosition)) {
            return false;
        }
        SourceFilePosition other = (SourceFilePosition) obj;
        return Objects.equal(mSourceFile, other.mSourceFile) &&
                Objects.equal(mSourcePosition, other.mSourcePosition);
    }
}
