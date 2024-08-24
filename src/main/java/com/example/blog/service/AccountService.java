package com.example.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import com.example.blog.Model.AccountVo;
import com.example.blog.repository.AccountRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private EmailService emailService;
    
//    @Autowired
//    private PasswordEncoder passwordEncoder; // 用於密碼加密和比對
    
    // 檢查用戶名是否已存在
    public boolean checkId(String username) {
        return accountRepository.findByUsername(username).isPresent();
    }

    // 檢查電子郵件是否已存在
    public boolean checkEmail(String email) {
        return accountRepository.findByEmail(email).isPresent();
    }
    
    // 檢查帳戶是否鎖定
    public boolean checkAccountLocked(String username) {
        return accountRepository.findByUsername(username)
                                .map(AccountVo::getAccountLocked)
                                .orElse(false);
    }
    
    public Optional<String> checkImageLink(String username) {
        return accountRepository.findImageLinkByUsername(username);
    }
    
    // 檢查使用者輸入密碼是否正確，錯誤則計數，滿三次鎖定帳戶
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    public ResponseEntity<String> checkUserPassword(String username, String inputPassword) {
        Optional<AccountVo> optionalUser = accountRepository.findByUsername(username);
        
        if (optionalUser.isPresent()) {
            AccountVo vo = optionalUser.get();

            if (vo.getPassword().equals(inputPassword)) {
               vo.setLoginAttempts(0); // 重置登錄嘗試次數
                accountRepository.save(vo);
                return ResponseEntity.ok("登入成功");
            } else {
                int attempts = vo.getLoginAttempts() + 1;
                vo.setLoginAttempts(attempts);

                if (attempts >= MAX_LOGIN_ATTEMPTS) {
                    vo.setAccountLocked(true);
                    accountRepository.save(vo);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("帳戶已被鎖定，請聯繫管理員");
                } else {
                    accountRepository.save(vo);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密碼錯誤 " + attempts + " 次");
                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用戶名或密碼錯誤");
    }
    
    // 保存帳戶信息
    public AccountVo saveAccount(AccountVo vo) {
        return accountRepository.save(vo);
    }
    
    // 插入新用戶資料
    public boolean insertUser(AccountVo vo) {
        try {
            String token = generateVerificationToken();
            vo.setVerificationToken(token);
            vo.setTokenExpiration(LocalDateTime.now().plusHours(24));
            accountRepository.save(vo);
            // 發送郵件
            emailService.sendVerificationEmail(vo, token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public ResponseEntity<String> registerUser(AccountVo vo) {
        // 檢查用戶名是否已存在
        if (accountRepository.findByUsername(vo.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("該用戶名已被使用!");
        }

        // 檢查電子郵件是否已存在
        if (accountRepository.findByEmail(vo.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("該電子信箱已被使用!");
        }

        // 插入新用戶資料
        try {       	
           if(insertUser(vo)) {        	   
        	   return ResponseEntity.ok("註冊成功! 請檢查您的電子郵件以完成註冊。");
           }
        } catch (Exception e) {       	
            return ResponseEntity.status(500).body("註冊失敗，請稍後重試。");
        }     
		return null;
    }

    // 根據用戶名查找帳戶
    public Optional<AccountVo> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    // 根據電子郵件查找帳戶
    public Optional<AccountVo> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    // 根據驗證 token 查找帳戶
    public Optional<AccountVo> findByVerificationToken(String verificationToken) {
        return accountRepository.findByVerificationToken(verificationToken);
    }
    
    //生成隨機驗證帳戶的token
    private String generateVerificationToken() {
        // 實現 token 生成邏輯，這裡只是一個示例
        return java.util.UUID.randomUUID().toString();
    }
    
    //設置生成的token至資料庫
    public void setVerificationToken(AccountVo vo) {
        String token = generateVerificationToken(); // 自定義方法生成驗證 token
        vo.setVerificationToken(token);
        vo.setTokenExpiration(LocalDateTime.now().plusHours(24));  // 設置24小時過期時間
        accountRepository.save(vo);  // 保存帳戶
    }
    
    // 驗證帳戶
    public boolean verifyAccount(String token) {
        Optional<AccountVo> accountOpt = findByVerificationToken(token);
        if (accountOpt.isPresent()) {
            AccountVo account = accountOpt.get();
            account.setIsVerified(true);
            account.setCreatedDate(LocalDateTime.now());
            account.setVerificationToken(null);  // 驗證後移除 token
            saveAccount(account);
            return true;
        }
        return false;
    }

	public String findImageLinkByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}