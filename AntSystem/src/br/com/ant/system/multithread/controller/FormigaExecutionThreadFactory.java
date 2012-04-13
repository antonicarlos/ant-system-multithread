/**
 *  This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.ant.system.multithread.controller;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class FormigaExecutionThreadFactory implements ThreadFactory {

	AtomicInteger	poolNumber	= new AtomicInteger(0);

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);

		poolNumber.set(poolNumber.get() + 1);
		thread.setName("MultiThreadFormigas " + poolNumber.get());
		thread.setPriority(Thread.MAX_PRIORITY);

		return thread;
	}

}
