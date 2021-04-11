/*
 * Copyright 2016 the original author or authors.
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
package com.sid.spring.boot.batch.component;

import org.springframework.batch.core.SkipListener;

/**
 * @author Lenovo
 */
public class CustomSkipListener implements SkipListener {
	@Override
	public void onSkipInRead(Throwable t) {
	}

	@Override
	public void onSkipInWrite(Object item, Throwable t) {
		System.out.println(">> Skipping " + item + " because writing it caused the error: " + t.getMessage());
	}

	@Override
	public void onSkipInProcess(Object item, Throwable t) {
		System.out.println(">> Skipping " + item + " because processing it caused the error: " + t.getMessage());
	}
}
