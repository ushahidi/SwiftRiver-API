/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ushahidi.swiftriver.core.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.ushahidi.swiftriver.core.data.dao.AccountDao;

import static org.mockito.Mockito.*;

public class AccountServiceTest {
	
	@Autowired
    private ApplicationContext applicationContext;

	@Test
	public void findById()
	{
		AccountDao mockAccountDao = mock(AccountDao.class);
		AccountService accountService = new AccountService();
		
		accountService.setAccountDao(mockAccountDao);
		accountService.getAccount(1);
		
		verify(mockAccountDao).findById(1);
	}
}
