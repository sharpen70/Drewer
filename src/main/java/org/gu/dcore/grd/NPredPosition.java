package org.gu.dcore.grd;
/*
 * Copyright (C) 2018 - 2020 Artificial Intelligence and Semantic Technology, 
 * Griffith University
 * 
 * Contributors:
 * Peng Xiao (sharpen70@gmail.com)
 * Zhe wang
 * Kewen Wang
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
import org.gu.dcore.model.Predicate;

public class NPredPosition {
	private Predicate predicate;
	private int index;
	
	public NPredPosition(Predicate p, int index) {
		this.predicate = p;
		this.index = index;
	}
	
	public Predicate getPredicate() {
		return this.predicate;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	@Override
	public String toString() {
		return "" + this.predicate + this.index;
	}
	
	@Override
	public int hashCode() {
		return this.predicate.hashCode() + this.index;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof NPredPosition)) return false;
		NPredPosition _obj = (NPredPosition)obj;
		
		if(this.predicate.equals(_obj.predicate)) 
			return this.index ==_obj.index;
		else return false;
	}
}
