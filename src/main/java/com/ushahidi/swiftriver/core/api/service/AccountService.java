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
package com.ushahidi.swiftriver.core.api.service;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;

@Transactional(readOnly = true)
@Service
public class AccountService {

	final Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private Mapper mapper;

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Get an account using its id
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException 
	 */
	public GetAccountDTO getAccountById(long id) throws NotFoundException {
		Account account = accountDao.findById(id);
		
		if (account == null) {
			throw new NotFoundException();
		}
		
		return mapGetAccountDTO(account);
	}

	/**
	 * Get an account by username
	 * 
	 * @param username
	 * @return
	 * @throws NotFoundException 
	 */
	public GetAccountDTO getAccountByUsername(String username) throws NotFoundException {
		Account account = accountDao.findByUsername(username);
		
		if (account == null) {
			throw new NotFoundException();
		}
		
		return mapGetAccountDTO(account);
	}
	
	/**
	 * Get an account by account_path
	 * 
	 * @param username
	 * @return
	 * @throws NotFoundException 
	 */
	public GetAccountDTO getAccountByName(String accountPath) throws NotFoundException {
		Account account = accountDao.findByName(accountPath);
		
		if (account == null) {
			throw new NotFoundException();
		}
		
		return mapGetAccountDTO(account);
	}
	
	/**
	 * Convert the given account into a GetAccountDTO
	 * @param account
	 * @return
	 */
	public GetAccountDTO mapGetAccountDTO(Account account) {
		GetAccountDTO accountDTO = mapper.map(account, GetAccountDTO.class);
		
		accountDTO.setFollowerCount(account.getFollowers().size());
		accountDTO.setFollowingCount(account.getFollowing().size());
		
		return accountDTO;
	}
}
