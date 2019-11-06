package demo.shiro.encrypt.controller;

import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.BlowfishCipherService;
import org.apache.shiro.crypto.DefaultBlockCipherService;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.*;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;

@RestController
@RequestMapping("codecAndCrypto")
@Slf4j
public class CodecAndCryptoTestController {

    String str = "hello";

    @RequestMapping("base64")
    public void testBase64() {
        String base64Encoded = Base64.encodeToString(str.getBytes());
        String str2 = Base64.decodeToString(base64Encoded);
        Assert.assertEquals(str, str2);
    }

    @RequestMapping("hex")
    public void testHex() {
        String hexEncoded = Hex.encodeToString(str.getBytes());
        String str2 = new String(Hex.decode(hexEncoded));
        Assert.assertEquals(str, str2);
    }

    @RequestMapping("codecSupport")
    public void testCodecSupport() {
        byte[] bytes = CodecSupport.toBytes(str, "utf-8");
        String str2 = CodecSupport.toString(bytes, "utf-8");
        Assert.assertEquals(str, str2);
    }

    @RequestMapping("random")
    public void testRandom() {
        // 生成随机数
        SecureRandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        randomNumberGenerator.setSeed("123".getBytes());
        log.info(randomNumberGenerator.nextBytes().toHex());
    }

    @RequestMapping("md5")
    public void testMd5() {
        String salt = "123";
        String hash = new Md5Hash(str, salt).toString();//还可以转换为 toBase64()/toHex()
        log.info("md5:"+hash);
    }

    @RequestMapping("sha1")
    public void testSha1() {
        String salt = "123";
        String hash = new Sha1Hash(str, salt).toString();
        log.info("sha1:"+hash);
    }

    @RequestMapping("sha256")
    public void testSha256() {
        String salt = "123";
        String hash = new Sha256Hash(str, salt).toString();
        log.info("sha256:"+hash);
    }

    @RequestMapping("sha384")
    public void testSha384() {
        String salt = "123";
        String hash = new Sha384Hash(str, salt).toString();
        log.info("sha384:"+hash);
    }

    @RequestMapping("sha512")
    public void testSha512() {
        String salt = "123";
        String hash = new Sha512Hash(str, salt).toString();
        log.info("sha512:"+hash);
    }

    @RequestMapping("simpleHash")
    public void testSimpleHash() {
        String salt = "123";
        String hash = new SimpleHash("SHA-1", str, salt).toString();
        log.info("simpleHash:"+hash);
    }

    @RequestMapping("hashService")
    public void testHashService() {
        String salt = "123";
        DefaultHashService hashService = new DefaultHashService();//默认算法SHA-512
        hashService.setHashAlgorithmName("SHA-512");
        hashService.setPrivateSalt(new SimpleByteSource(salt));//私盐，默认无
        hashService.setGeneratePublicSalt(true);//是否生成公盐，默认false
        hashService.setRandomNumberGenerator(new SecureRandomNumberGenerator());//用于生成公盐。默认就是这个
        hashService.setHashIterations(1);//生成Hash值的迭代次数

        HashRequest request = new HashRequest.Builder()
                .setAlgorithmName("MD5").setSource(ByteSource.Util.bytes("hello"))
                .setSalt(ByteSource.Util.bytes("123")).setIterations(2).build();

        /**
         * 1.algorithmName = request.algorithmName != null ? request.algorithmName : hashService.algorithmName
         * 2.request.setSalt()，request设置的是公盐，如果没有公盐，会根据 privateSaltExists || isGeneratePublicSalt() 生成一个随机数作为公盐，最终会将公盐和私盐合并
         * 3.iterations，优先取request中设置的hash迭代次数，但是如果小于1的话，则取service中设置的，会保证最小是1
         */
        String hex = hashService.computeHash(request).toHex();
        log.info("hashService:"+hex);
    }

    @RequestMapping("aesCipherService")
    public void testAesCipherService() {
        AesCipherService aesCipherService = new AesCipherService();
        aesCipherService.setKeySize(128);//设置key长度

        // 生成key
        Key key = aesCipherService.generateNewKey();
        String text = "hello";

        // 加密
        String encryptText = aesCipherService.encrypt(text.getBytes(), key.getEncoded()).toHex();
        // 解密
        String text2 = new String(aesCipherService.decrypt(Hex.decode(encryptText), key.getEncoded()).getBytes());

        Assert.assertEquals(text, text2);
    }

    @RequestMapping("blowfishCipherService")
    public void testBlowfishCipherService() {
        BlowfishCipherService blowfishCipherService = new BlowfishCipherService();
        blowfishCipherService.setKeySize(128);

        //生成key
        Key key = blowfishCipherService.generateNewKey();

        String text = "hello";

        //加密
        String encrptText = blowfishCipherService.encrypt(text.getBytes(), key.getEncoded()).toHex();
        //解密
        String text2 = new String(blowfishCipherService.decrypt(Hex.decode(encrptText), key.getEncoded()).getBytes());

        Assert.assertEquals(text, text2);
    }

    @RequestMapping("defaultBlockCipherService")
    public void testDefaultBlockCipherService() {
        //对称加密，cipherService使用Java的JCA（javax.crypto.Cipher）加密API，常见的如 'AES', 'Blowfish'
        DefaultBlockCipherService cipherService = new DefaultBlockCipherService("AES");
        cipherService.setKeySize(128);

        //生成key
        Key key = cipherService.generateNewKey();

        String text = "hello";

        //加密
        String encrptText = cipherService.encrypt(text.getBytes(), key.getEncoded()).toHex();
        //解密
        String text2 = new String(cipherService.decrypt(Hex.decode(encrptText), key.getEncoded()).getBytes());

        Assert.assertEquals(text, text2);
    }
}