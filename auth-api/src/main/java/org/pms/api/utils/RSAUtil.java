package org.pms.api.utils;

import org.pms.types.AuthConstants;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA密钥工具类（公钥加载）
 * 用于Gateway和WS服务加载公钥进行JWT验签
 *
 * @author alcsyooterranf
 * @version 1.0
 * @since 2024/5/29 下午10:31
 */
public class RSAUtil {
	
	private static final String KEY_PATH = AuthConstants.KEY_PATH;
	private static final String ALGORITHM = AuthConstants.ALGORITHM;
	private static final String PUBLIC_KEY_FILENAME = AuthConstants.PUBLIC_KEY_FILENAME;
	
	/**
	 * 加载RSA公钥（用于验签）
	 *
	 * @return 公钥
	 * @throws Exception 加载失败时抛出异常
	 */
	public static PublicKey getPublicKey() throws Exception {
		return loadPublicKey(KEY_PATH + PUBLIC_KEY_FILENAME);
	}
	
	/**
	 * 从文件中读取公钥
	 *
	 * @param fileName 公钥保存路径
	 * @return 公钥对象
	 * @throws Exception 读取失败时抛出异常
	 */
	private static PublicKey loadPublicKey(String fileName) throws Exception {
		String publicKey = readFile(fileName);
		return base642PublicKey(publicKey);
	}
	
	/**
	 * 将Base64字符串转换为公钥对象
	 *
	 * @param publicKey64 Base64编码的公钥字符串
	 * @return 公钥对象
	 * @throws NoSuchAlgorithmException 算法不存在
	 * @throws InvalidKeySpecException  密钥规格无效
	 */
	private static PublicKey base642PublicKey(String publicKey64) throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey64));
		return keyFactory.generatePublic(keySpec);
	}
	
	/**
	 * 从文件读取内容
	 *
	 * @param filename 文件路径
	 * @return 文件内容
	 * @throws IOException 读取失败
	 */
	private static String readFile(String filename) throws IOException {
		try (FileReader fileReader = new FileReader(filename)) {
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

