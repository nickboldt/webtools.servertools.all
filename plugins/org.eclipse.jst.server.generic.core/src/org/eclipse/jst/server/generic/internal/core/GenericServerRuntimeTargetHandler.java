/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved. � This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Gorkem Ercan - initial API and implementation
 *     Naci M. Dai
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL ETERATION A.S. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Eteration Bilisim A.S.  For more
 * information on eteration, please see
 * <http://www.eteration.com/>.
 ***************************************************************************/
package org.eclipse.jst.server.generic.internal.core;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.core.ClasspathRuntimeTargetHandler;

import org.eclipse.wst.server.core.IRuntime;

public class GenericServerRuntimeTargetHandler extends
		ClasspathRuntimeTargetHandler {

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.java.core.ClasspathRuntimeTargetHandler#getId()
	 */
	public String getId() {
		return "org.eclipse.jst.server.generic.runtime";
	}          

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.java.core.ClasspathRuntimeTargetHandler#getClasspathContainerLabel(com.ibm.wtp.server.core.IRuntime, java.lang.String)
	 */
	public String getClasspathContainerLabel(IRuntime runtime, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.java.core.ClasspathRuntimeTargetHandler#resolveClasspathContainer(com.ibm.wtp.server.core.IRuntime, java.lang.String)
	 */
	public IClasspathEntry[] resolveClasspathContainer(IRuntime runtime,
			String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
