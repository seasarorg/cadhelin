/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.cadhelin.interceptor;

import javax.transaction.TransactionManager;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.cadhelin.ControllerContext;
import org.seasar.extension.tx.AbstractTxInterceptor;

/**
 * �g�����U�N�V������v�����郁�\�b�h�̂��߂̃C���^�[�Z�v�^�ł��B
 * <p>
 * ���̃C���^�[�Z�v�^���K�p���ꂽ���\�b�h���Ăяo���ꂽ�ۂɃg�����U�N�V�������J�n����Ă��Ȃ��ꍇ�́A�g�����U�N�V�������J�n����܂��B ���\�b�h���I��
 * (��O���X���[�����ꍇ��) ������A�J�n�����g�����U�N�V�����͊��� (�R�~�b�g�܂��̓��[���o�b�N) ����܂��B<br>
 * ���\�b�h���Ăяo���ꂽ�ۂɁA���Ƀg�����U�N�V�������J�n����Ă����ꍇ�͉������܂���B
 * </p>
 * 
 * @author higa
 * 
 */
public class RequiredInterceptor extends AbstractTxInterceptor {
	private boolean rollbackWhenError = true;
	public void setRollbackWhenError(boolean rollbackWhenError) {
		this.rollbackWhenError = rollbackWhenError;
	}
    /**
     * �C���X�^���X���\�z���܂��B
     * 
     */
    public RequiredInterceptor() {
    	super();
    	System.out.println("RequiredInterceptor");
    }

    public Object invoke(final MethodInvocation invocation) throws Throwable {
        boolean began = false;
        if (!hasTransaction()) {
            begin();
            began = true;
        }
        Object ret = null;
        try {
            ret = invocation.proceed();
            if (began) {
                if(rollbackWhenError && 
                		0<ControllerContext.getContext().getErrorCount()){
                	rollback();
                }else{
                    end();                	
                }
            }
            return ret;
        } catch (final Throwable t) {
            if (began) {
                complete(t);
            }
            throw t;
        }
    }

}
