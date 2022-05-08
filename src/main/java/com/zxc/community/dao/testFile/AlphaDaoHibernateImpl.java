package com.zxc.community.dao.testFile;

import com.zxc.community.dao.AlphaDao;
import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
