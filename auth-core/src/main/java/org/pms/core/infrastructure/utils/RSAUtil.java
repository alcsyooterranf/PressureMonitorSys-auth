package org.pms.core.infrastructure.utils;

import com.pms.types.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/**
 * RSA密钥工具类（Auth服务专用）
 * Auth服务持有私钥和公钥，用于签发JWT
 *
 * @author alcsyooterranf
 */
@Slf4j
public class RSAUtil {
	
	private static final String ALGORITHM = Constants.ALGORITHM;
	private static final String PUBLIC_KEY_FILENAME = Constants.PUBLIC_KEY_FILENAME;
	private static final String PRIVATE_KEY_FILENAME = Constants.PRIVATE_KEY_FILENAME;
	// TODO: 静态变量
	private static final String LOCAL_KEY_PATH = "data/keys";
	
	/**
	 * 生成RSA公私钥
	 *
	 * @return keyMap
	 */
	public static KeyPair getKeyPair() throws NoSuchAlgorithmException, IOException {
		KeyPair keyPair = null;
		try {
			keyPair = loadKey();
		} catch (Exception e) {
			log.error("load key failed: {}, create new key", e.getMessage());
			keyPair = createKey();
			saveKey(keyPair);
		} finally {
			log.debug("RSA key: {}", keyPair);
		}
		return keyPair;
	}
	
	// 生成RSA公私钥
	// <其中salt为自定义字符串，相对而言越复杂越好>
	private static KeyPair createKey() throws NoSuchAlgorithmException {
		// 首先生成一个KeyPairGenerator对象，用于生成非对称公私钥，实例化时指定类型为“RSA”
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
		// 根据salt创建一个随机源
		String salt = String.valueOf(System.currentTimeMillis());
		SecureRandom secureRandom = new SecureRandom(salt.getBytes());
		// 对KeyPairGenerator对象执行初始化，其中2048代表密钥大小，新版本JWT至少要2048长度；参数二为随机源
		keyPairGenerator.initialize(2048, secureRandom);
		// 生成公私钥，“genKeyPair()”方法与“generateKeyPair()”方法相同，都能用
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		log.debug("rsaKey: {}", keyPair);
		
		log.info("create key success");
		return keyPair;// 若需要将密钥写入文件，可以对生成的公私钥执行“对象名.getEncoded()”方法将密钥转换为“byte[]”，再写入文件
	}
	
	private static void saveKey(KeyPair key) throws IOException {
		File dir = new File(LOCAL_KEY_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 使用 File 构造器正确拼接路径，避免跨平台路径分隔符问题
		File publicKeyFile = new File(dir, PUBLIC_KEY_FILENAME);
		File privateKeyFile = new File(dir, PRIVATE_KEY_FILENAME);

		try (OutputStreamWriter publicWriter = new OutputStreamWriter(
				new FileOutputStream(publicKeyFile), StandardCharsets.UTF_8)) {
			publicWriter.write(key2Base64(key.getPublic()));
			publicWriter.flush();
		}

		try (OutputStreamWriter privateWriter = new OutputStreamWriter(
				new FileOutputStream(privateKeyFile), StandardCharsets.UTF_8)) {
			privateWriter.write(key2Base64(key.getPrivate()));
			privateWriter.flush();
		}
	}
	
	/**
	 * 将Key转为Base64字符串
	 *
	 * @param key key
	 * @return base64
	 */
	private static String key2Base64(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	/**
	 * 从文件读取密钥
	 *
	 * @return keyMap
	 * @throws Exception e
	 */
	private static KeyPair loadKey() throws Exception {
		// 使用 File 构造器正确拼接路径，避免跨平台路径分隔符问题
		File dir = new File(LOCAL_KEY_PATH);
		File publicKeyFile = new File(dir, PUBLIC_KEY_FILENAME);
		File privateKeyFile = new File(dir, PRIVATE_KEY_FILENAME);

		PublicKey publicKey = loadPublicKey(publicKeyFile.getPath());
		PrivateKey privateKey = loadPrivateKey(privateKeyFile.getPath());
		if (ObjectUtils.isEmpty(publicKey) || ObjectUtils.isEmpty(privateKey)) {
			throw new Exception("load key failed");
		}
		KeyPair keyMap = new KeyPair(publicKey, privateKey);
		log.info("load key finished");
		return keyMap;
	}
	
	
	/**
	 * 从文件中读取公钥
	 *
	 * @param filename 公钥保存路径，相对于classpath
	 * @return 公钥对象
	 * @throws Exception e
	 */
	private static PublicKey loadPublicKey(String filename) throws Exception {
		String publicKey = readFile(filename);
		return base642PublicKey(publicKey);
	}
	
	/**
	 * 从文件中读取私钥
	 *
	 * @param filename 私钥保存路径，相对于classpath
	 * @return 私钥对象
	 * @throws Exception
	 */
	private static PrivateKey loadPrivateKey(String filename) throws Exception {
		String privateKey = readFile(filename);
		return base642PrivateKey(privateKey);
	}
	
	private static PublicKey base642PublicKey(String publicKey64) throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		//base64转key
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey64));
		return keyFactory.generatePublic(keySpec);
	}
	
	private static PrivateKey base642PrivateKey(String privateKey64) throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		//base64转key
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey64));
		return keyFactory.generatePrivate(keySpec);
	}
	
	private static String readFile(String filename) throws IOException {
		try (
				FileReader fileReader = new FileReader(filename)
		) {
			int data = fileReader.read();
			StringBuilder sb = new StringBuilder();
			while (data != -1) {
				sb.append((char) data);
				data = fileReader.read();
			}
			return sb.toString();
		}
	}
	
}