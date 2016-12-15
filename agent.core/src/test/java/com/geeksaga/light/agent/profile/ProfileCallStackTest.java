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

import org.junit.Test;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


/**
 * @author geeksaga
 */
public class ProfileCallStackTest
{
    private ProfileMethod root = new ProfileMethod();

    @Test
    public void testPush()
    {
        ProfileCallStack profileCallStack = new ProfileCallStack(root);

        assertThat(profileCallStack.push(root), is(1));
        assertThat(profileCallStack.push(new ProfileMethod()), is(2));
    }

    @Test
    public void testPop()
    {
        ProfileCallStack profileCallStack = new ProfileCallStack(root);
        profileCallStack.push(root);

        ProfileMethod poppedProfile = (ProfileMethod) profileCallStack.pop();

        assertThat(root, sameInstance(poppedProfile));
    }

    @Test
    public void testPopFailover()
    {
        ProfileCallStack profileCallStack = new ProfileCallStack(root);
        profileCallStack.push(new ProfileMethod());
        profileCallStack.pop();

        ProfileMethod poppedProfile = (ProfileMethod) profileCallStack.pop();

        assertThat(poppedProfile, nullValue());

        ProfileMethod newProfile = new ProfileMethod();
        profileCallStack.push(newProfile);

        assertThat(newProfile, sameInstance(profileCallStack.pop()));
    }

    @Test
    public void testPushPopOrder()
    {
        ProfileCallStack profileCallStack = new ProfileCallStack(root);

        ProfileMethod profile1 = new ProfileMethod();
        ProfileMethod profile2 = new ProfileMethod();
        ProfileMethod profile3 = new ProfileMethod();

        profileCallStack.push(profile1);
        profileCallStack.push(profile2);
        profileCallStack.push(profile3);

        assertThat(profile3, sameInstance(profileCallStack.pop()));
        assertThat(profile2, sameInstance(profileCallStack.pop()));
        assertThat(profile1, sameInstance(profileCallStack.pop()));
    }

    @Test
    public void testMaxSize()
    {
        ProfileCallStack profileCallStack = new ProfileCallStack(root, 3);

        ProfileMethod profile1 = new ProfileMethod();
        ProfileMethod profile2 = new ProfileMethod();
        ProfileMethod profile3 = new ProfileMethod();
        ProfileMethod profile4 = new ProfileMethod();
        ProfileMethod profile5 = new ProfileMethod();

        assertThat(profileCallStack.push(profile1), is(1));
        assertThat(profileCallStack.push(profile2), is(2));
        assertThat(profileCallStack.push(profile3), is(3));
        assertThat(profileCallStack.push(profile4), is(4));
        assertThat(profileCallStack.push(profile5), is(5));
        assertThat(profileCallStack.push(new ProfileMethod()), is(6));

        assertThat(root, sameInstance(profileCallStack.pop()));
        assertThat(root, sameInstance(profileCallStack.pop()));
        assertThat(root, sameInstance(profileCallStack.pop()));
        assertThat(profile3, sameInstance(profileCallStack.pop()));
        assertThat(profile2, sameInstance(profileCallStack.pop()));
        assertThat(profile1, sameInstance(profileCallStack.pop()));
    }

    @Test
    public void testPushPopLarge()
    {
        ProfileCallStack profileCallStack = new ProfileCallStack(root);

        int count = 10000;
        for (int i = 0; i < count; i++)
        {
            assertThat(profileCallStack.push(new ProfileMethod()), is(i + 1));
        }

        assertThat(profileCallStack.push(new ProfileMethod()), is(10001));

        for (int i = 0; i < count; i++)
        {
            profileCallStack.pop();
        }

        profileCallStack.pop();

        assertThat(profileCallStack.pop(), nullValue());
    }
}