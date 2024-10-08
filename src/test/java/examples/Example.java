package examples;

import tk.wiq.*;
import tk.wiq.crypt.AESKey;
import tk.wiq.crypt.IvPS;
import tk.wiq.io.AsyncFileDecryptor;
import tk.wiq.io.AsyncFileEncryptor;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static CompletableFuture<String> encrypt(String input) throws Exception {
        CompletableFuture<AESKey> key = AESKey.createAsynchronously(256); // You should use 128 or 256 bits
        CompletableFuture<IvPS> ivPS = IvPS.createAsynchronously(16); // Value must be not higher than 16
        
        // Any other activity ...
        
        key.join();
        ivPS.join();
        
        System.out.println("Key: " + key.get().getKey()); // Key for decrypt. Can be used with AESKey key = new AESKey(key_here)
        System.out.println("IvPS: " + ivPS.get().getIv()); // IvPS for decrypt. Can be used with IvPS ivPS = new IvPS(IvPS_here)
        
        return TextEncryptor.encrypt(input, key.get(), ivPS.get());
    }
    
    public static CompletableFuture<String> decrypt(String input, AESKey key, IvPS ivPS) {
        return TextEncryptor.decrypt(input, key, ivPS);
    }
    
    public static void encryptFile(File file) throws Exception {
        CompletableFuture<AESKey> key = AESKey.createAsynchronously(256);
        CompletableFuture<IvPS> ivPS = IvPS.createAsynchronously(16);
        
        // Any activity here ...
        
        AsyncFileEncryptor encryptor = new AsyncFileEncryptor(file);
        encryptor.deleteOnEncrypt(false); // Should file be deleted after encryption? Default false
        
        key.join();
        ivPS.join();
        
        System.out.println("Key: " + key.get().getKey());
        System.out.println("IvPS: " + ivPS.get().getIv());
        
        encryptor.setSpec(ivPS.get());
        encryptor.setKey(key.get());
        encryptor.encrypt(); // Will be created file with .encrypted extension and encrypted content
    }
    
    public static void decryptFile(File file, AESKey key, IvPS ivPS) throws Exception {
        AsyncFileDecryptor decryptor = new AsyncFileDecryptor(file);
        decryptor.setKey(key);
        decryptor.setSpec(ivPS);
        decryptor.decrypt(); // Will be created file with .decrypted extension
    }
}
