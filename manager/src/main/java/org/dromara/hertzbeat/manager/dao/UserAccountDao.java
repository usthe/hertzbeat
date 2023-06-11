package org.dromara.hertzbeat.manager.dao;

/**
 * @author:Li Jinming
 * @Description: 用户信息DAO
 * @date:2023-06-07
 */

import org.dromara.hertzbeat.manager.pojo.dto.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface UserAccountDao extends JpaRepository<UserAccount, Long>, JpaSpecificationExecutor<UserAccount> {

    /**
     * delete accounts by account id
     *
     * @param ids id list
     */
    void deleteAllByIdIn(Set<Long> ids);

    /**
     * find account by account id
     *
     * @param ids id list
     * @return account list
     */
    List<UserAccount> findByIdIn(Set<Long> ids);


    /**
     * delete user account by user name
     *
     * @param userName
     */
    void deleteUserAccountsByIdentifierEquals(String userName);

    /**
     * find user account by user name
     *
     * @param userName
     * @return account list
     */
    List<UserAccount> findUserAccountsByIdentifierEquals(String userName);

}
