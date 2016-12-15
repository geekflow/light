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
package com.geeksaga.light.agent.profile;

/**
 * @author geeksaga
 */
public class ProfileCallStack
{
    private static final int STACK_SIZE = 128;
    private static final int DEFAULT_INDEX = 0;

    private ProfileData[] stack = new ProfileData[STACK_SIZE];
    private final ProfileData rootProfile;
    private final int maxStackSize;
    private int index = DEFAULT_INDEX;
    private int overflowIndex = 0;
    private int sequence;

    public ProfileCallStack(ProfileData rootProfile)
    {
        this(rootProfile, -1);
    }

    public ProfileCallStack(ProfileData rootProfile, int maxStackSize)
    {
        this.rootProfile = rootProfile;
        this.maxStackSize = maxStackSize;
    }

    public ProfileData getRoot()
    {
        return rootProfile;
    }

    public int push(final ProfileData profileData)
    {
        if (isOverflow())
        {
            overflowIndex++;
            return index + overflowIndex;
        }

        checkExtend(index + 1);

        stack[index++] = profileData;

        profileData.setSequence(sequence++);
        profileData.setLevel(index);

        return index;
    }

    public ProfileData pop()
    {
        if (isOverflow() && overflowIndex > 0)
        {
            overflowIndex--;
            return rootProfile;
        }

        final ProfileData data = peek();
        if (data != null)
        {
            stack[index - 1] = null;
            index--;
        }

        return data;
    }

    private ProfileData peek()
    {
        if (index == DEFAULT_INDEX)
        {
            return null;
        }

        if (isOverflow() && overflowIndex > 0)
        {
            return rootProfile;
        }

        return stack[index - 1];
    }

    private void checkExtend(final int size)
    {
        final ProfileData[] originalStack = this.stack;
        if (size >= originalStack.length)
        {
            final int copyStackSize = originalStack.length << 1;
            final ProfileData[] copyStack = new ProfileData[copyStackSize];
            System.arraycopy(originalStack, 0, copyStack, 0, originalStack.length);
            this.stack = copyStack;
        }
    }

    public boolean isEmpty()
    {
        return index == DEFAULT_INDEX;
    }

    private boolean isOverflow()
    {
        return maxStackSize != -1 && maxStackSize <= index;
    }
}