package com.demo.service.impl;

import com.demo.UserEntity;
import com.demo.dao.IUserDAO;
import com.demo.service.IUserService;
import com.demo.service.IUserService2;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
public class IUserServiceImpl implements IUserService {

    @Resource
    IUserDAO userDAO;

    @Resource
    IUserService2 userService2;
    /**
     * fun1()默认PROPAGATION_REQUIRED
     * funNest() PROPAGATION_NESTED 无异�?
     * 
     */
    @Override
    @Transactional
    public void fun1() throws Exception {

        //数据库操�?
        funNone();
        //调用另一个service的方�?
        userService2.funNest();
    }
    
    /**
     * fun2()默认PROPAGATION_REQUIRED
     * funNestException() PROPAGATION_NESTED 异常
     * 
     */
    @Override
    @Transactional
    public void fun2() throws Exception {
        //嵌套事务的使用场�?
        funNone();
        //当所调用的方法为NESTED事务,该事务的回滚可以不影响到调用者的事务
        //当然如果没有catch exception,异常冒泡而出,就将触发调用者事务的回滚
        userService2.funNestException();
       // userService2.funRequire();


    }
    
    /**
     * fun2_2()默认PROPAGATION_REQUIRED
     * funNestException() PROPAGATION_NESTED 异常
     * 
     */
    @Override
    @Transactional
    public void fun2_2() throws Exception {
        //嵌套事务的使用场�?
        funNone();

        try {
            //当所调用的方法为NESTED事务,该事务的回滚可以不影响到调用者的事务
            //当然如果没有catch exception,异常冒泡而出,就将触发调用者事务的回滚
            userService2.funNestException();
        } catch (Exception e) {
            //do something
        }

        userService2.funRequire();


    }

    /**
     * fun2_2()默认PROPAGATION_REQUIRED
     * funNestException() PROPAGATION_NESTED 
     * 外部事务异常
     */
    @Override
    @Transactional
    public void fun3() throws Exception {

        funNone();
        //调用的事务为NESTED事务的方�?
        userService2.funNest();

        //此时在调用�?�处,触发�?个unchecked异常
        throwExcp();


        //此时会发现包括调用的userService2.funNest()也被回滚�?
        //也就是说,当调用的方法是NESTED事务,该方法抛出异常如果得到了处理(try-catch),那么该方法发生异常不会触发整个方法的回滚
        //而调用�?�出现unchecked异常,却能触发�?调用的nested事务的回�?.
    }
    
    @Override
    @Transactional
    public void fun4() throws Exception {

        //数据库操�?
        funNone();
        //调用RequireNew类型事务的方�?,调用者的异常回滚不会影响到它
        userService2.funRequireNew();
        //数据库操�?
        funNone();
    }

    @Override
    @Transactional
    public void fun4_2() throws Exception {
        //而REQUIRES_NEW,当被调用�?,就相当于暂停(挂起)当前事务,
        //先开�?个新的事务去执行REQUIRES_NEW的方�?,如果REQUIRES_NEW异常
        funNone();
     
        userService2.funRequireNewException();
       
    }

    @Override
    @Transactional
    public void fun4_3() throws Exception {
        //而REQUIRES_NEW,当被调用�?,就相当于暂停(挂起)当前事务,先开�?个新的事务去执行REQUIRES_NEW的方�?,如果REQUIRES_NEW中的异常得到了处�?
        //那么他将不影响调用�?�的事务,同时,调用者之后出现了异常,同样也不会影响之前调用的REQUIRES_NEW方法的事�?.

        //不会回滚
        funNone();
        try {
            //当异常得到处�?
            userService2.funRequireNewException();
        } catch (Exception e) {

        }
    }

    @Override
    @Transactional
    public void fun5() throws Exception {

        //数据库操�?
        funNone();
        //调用RequireNew类型事务的方�?,调用者的异常回滚不会影响到它
        userService2.funRequireNew();
        //数据库操�?
        funNone();

        //抛出unchecked异常,触发回滚
        throwExcp();


    }

    @Override
    @Transactional
    public void fun6() throws Exception {
        
        funNone();
      
        userService2.funRequireException();
       
    }
    
    @Override
    @Transactional
    public void fun6_2() throws Exception {

        funNone();
        
        try {
            //当调用的是Required�?,就算异常被处理了,整个方法也将会回�?
            userService2.funRequireException();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        funNone();
    }


    @Override
    @Transactional
    public void fun7() throws Exception {

        funRequire();

        try {
            funNestException();
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

        funRequire();

    }

    @Override
    @Transactional
    public void fun8() throws Exception {
        ((IUserService) AopContext.currentProxy()).funRequire();

        try {
            ((IUserService) AopContext.currentProxy()).funNestException();
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

        ((IUserService) AopContext.currentProxy()).funRequire();
    }


    //不带事务的方�?
    public void funNone() throws Exception {
        save(new UserEntity("IUserService_None"));

    }

    @Override
    public void funNoneException() throws Exception {
        save(new UserEntity("IUserService_NoneException"));
        throwExcp();
    }


    //启动默认事务的方�?
    @Transactional(propagation = Propagation.REQUIRED)
    public void funRequire() throws Exception {
        save(new UserEntity("IUserService_Require"));

    }

    //启动默认事务的方�?
    @Transactional(propagation = Propagation.REQUIRED)
    public void funRequire2() throws Exception {
        save(new UserEntity("IUserService_Require2"));

    }

    //启动默认事务的方�?,抛出RuntimeException
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void funRequireException() throws Exception {
        save(new UserEntity("IUserService_RequireException"));

        throwExcp();

    }

    //启动嵌套事务的方�?
    @Transactional(propagation = Propagation.NESTED)
    public void funNest() throws Exception {
        save(new UserEntity("IUserService_Nest"));

    }


    //启动嵌套事务的方�?,但会抛出异常
    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void funNestException() throws Exception {
        save(new UserEntity("IUserService_NestException"));
        throwExcp();
    }

    //REQUIRES_NEW事务的方�?
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void funRequireNew() throws Exception {
        save(new UserEntity("IUserService_RequireNew"));

    }

    //REQUIRES_NEW事务的方�?,但会抛出异常
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void funRequireNewException() throws Exception {
        save(new UserEntity("IUserService_RequireNewException"));
        throwExcp();
    }


    //抛出异常
    private void throwExcp() throws Exception {
        throw new RuntimeException("IUserService_boom");
    }

    //保存数据
    public int save(UserEntity userEntity) throws Exception {
        userDAO.save(userEntity);
        UserEntity ue = userDAO.selectById(userEntity.getId());
        System.out.println(ue);
        return userEntity.getId();
    }
}