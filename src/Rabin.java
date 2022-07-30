import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Rabin {

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
    // Blum prime random numbers generation
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	static Random gRandomGen = new Random();
	
	public static BigInteger generateRandomNumber(int numBits) {
		// Constructs a randomly generated BigInteger, uniformly distributed over the range 0 to (2^numBits - 1), inclusive.
		return new BigInteger(numBits, 10, gRandomGen);
	}
	
	// Test Miller-Rabin
    public static boolean testPrimeNumber(BigInteger p) {       
        // step 0
        BigInteger pMinus1 = p.subtract(BigInteger.ONE);
        ///System.out.println("Test Miller-Rabin: Step 0:");
        ///System.out.println("Test Miller-Rabin: p - 1 = " + pMinus1.toString());
        // find s
        int s = 0;
        // divide by 2
        while (pMinus1.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
        	s++;
        	pMinus1 = pMinus1.divide(BigInteger.TWO);
        }
        BigInteger d = pMinus1;
        ///System.out.println("Test Miller-Rabin: d = " + d.toString());
        ///System.out.println("Test Miller-Rabin: s = " + s);
        
        pMinus1 = p.subtract(BigInteger.ONE); // refresh value
        
        for (int k = 1; k < 10; k++) {
	        // step 1
	        ///System.out.println("Test Miller-Rabin: step 1, k = " + k);
	        BigInteger x = generateRandomNumber(p.bitLength() - 70);
	        
	        if (x.equals(BigInteger.ZERO) || x.equals(BigInteger.ONE) || x.equals(pMinus1)) {
	        	System.out.println("Test Miller-Rabin 1: bad number - generate one more time");
	        	continue;
	        }
	        
	        BigInteger resGcd = x.gcd(p);
	        if (!resGcd.equals(BigInteger.ONE)) {
	        	System.out.println("Test Miller-Rabin 1: number failed - not prime");
	        	return false;
	        }
	        
	        // step 2
	        ///System.out.println("Test Miller-Rabin: step 2");
	        BigInteger x_r = x.modPow(d, p);
	        //System.out.println("Test Miller-Rabin 2: x_r = " + x_r.toString());
	        // step 2.1
	        if (x_r.equals(BigInteger.ONE) || x_r.equals(pMinus1)) {
	        	//System.out.println("Test Miller-Rabin 2.1: number is pseudosimple : x^d = +-1(mod p)");
	        } else {
		        // step 2.2
		        for (int r = 1; r < s; r++) {
		        	x_r = x_r.modPow(BigInteger.TWO, p);
		        	
		        	if (x_r.equals(pMinus1)) {
			        	///System.out.println("Test Miller-Rabin 2.2: number is pseudosimple : x^(d*2^r) = -1(mod p)");
			        	continue;
		        	}
		        	
		        	if (x_r.equals(BigInteger.ONE)) {
		        		///System.out.println("Test Miller-Rabin 2.2: number failed - not prime, r = " + r);
		        		return false;
		        	}
		        }
		        ///System.out.println("Test Miller-Rabin: number failed - not prime. Step 2.1 and 2.2 failed");
		        ///System.out.println("Test Miller-Rabin: x_r = " + x_r.toString());
		        return false;
	        }
	    
        }
        
        return true;
    }
	
    
    public static boolean isBlumNumber(BigInteger p) {
    	// p should be 4*k + 3
    	
    	BigInteger three = BigInteger.valueOf(3);
    	BigInteger pMinus3 = p.subtract(three);
        int s = 0;
        // divide by 2
        while (pMinus3.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
        	s++;
        	pMinus3 = pMinus3.divide(BigInteger.TWO);
        }
        
        // s should be 2 as 2^2 = 4
        if (s == 2) {
        	//System.out.println("blume is true");
        	return true;
        } else {
        	//System.out.println("blume is false");
        	return false;
        }
    }
    
	public static BigInteger generateBlumPrimeNumber(int numBits) {
		BigInteger newRndNumber = BigInteger.TWO; // just to avoid errors - set NOT prime number 
		boolean isPrime = false;
		boolean isBlum = false;
		while (isPrime == false || isBlum == false) {
			newRndNumber = generateRandomNumber(numBits);
			//System.out.println("generateBlumPrimeNumber: posible prime number " + newRndNumber.toString(16));
			isPrime = testPrimeNumber(newRndNumber);
			isBlum = isBlumNumber(newRndNumber);
			
		}
		//System.out.println("generateBlumPrimeNumber: new random prime Blum number = " + newRndNumber.toString(16));
		return newRndNumber;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
    // Rabin functions
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static int jacobiSymbol(BigInteger k, BigInteger n) {

        k = k.mod(n);
        
        int jacobi = 1;
        while (k.compareTo(BigInteger.ZERO) == 1) {
            while (k.mod(BigInteger.TWO).equals(BigInteger.ZERO) == true) {
            	k = k.divide(BigInteger.TWO);
            	var r = n.mod(BigInteger.valueOf(8));
                if (r.equals(BigInteger.valueOf(3)) || r.equals(BigInteger.valueOf(5))) {
                    jacobi = -jacobi;
                }
            }
            BigInteger temp = n;
            n = k;
            k = temp;
            if (k.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)) 
            					&& n.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
                jacobi = -jacobi;
            }
            k = k.mod(n);
        }
        if (n.equals(BigInteger.ONE)) {
            return jacobi;
        }
        return 0;
    }
	
	public static int iversonSymbol(int jacobiSymbol) {
		if (jacobiSymbol == 1) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public static BigInteger formatMsg(BigInteger msg, BigInteger n) {
		int l = 0;
		if ((n.bitLength() % 8) == 0) {
			l = n.bitLength() / 8;
		} else {
			l = n.bitLength() / 8 + 1;
		}
		//System.out.println("formatMsg: n.bitLen = " + n.bitLength());
		//System.out.println("formatMsg: l = " + l);
		BigInteger formatedMsg = BigInteger.valueOf(255); // 0xFF
		formatedMsg = formatedMsg.shiftLeft(8 * (l - 2)); // 0x00||0xFF||0x000000....00000
		formatedMsg = formatedMsg.add(msg.shiftLeft(64)); // 0x00||0xFF||   m   ||0x00..00
		BigInteger r = generateRandomNumber(64);
		formatedMsg = formatedMsg.add(r); // 0x00||0xFF||   m   || r
		return formatedMsg;
	}
	
	public static BigInteger unformatMsg(BigInteger formatedMsg, BigInteger n) {
		int l = 0;
		if ((n.bitLength() % 8) == 0) {
			l = n.bitLength() / 8;
		} else {
			l = n.bitLength() / 8 + 1;
		}
		// formatedMsg has format   0x00||0xFF||   m   || r
		
		// remove leading FF byte
		BigInteger msg = formatedMsg.subtract(BigInteger.valueOf(255).shiftLeft(8 * (l - 2))); // 0x00||0x00||   m   || r
		// remove r
		msg = msg.shiftRight(64); // 0x00||0x00|| m 
		
		return msg;
	}
	
    public static ArrayList<BigInteger> GenerateKeyPair(int keySize) {
    	assert (keySize % 2) == 0;
    	BigInteger p = generateBlumPrimeNumber(keySize / 2);
    	BigInteger q = generateBlumPrimeNumber(keySize / 2);
    	BigInteger n = p.multiply(q);
    	// Generate b that has less key size
    	BigInteger b = generateBlumPrimeNumber(keySize - 20);

    	ArrayList<BigInteger> keys = new ArrayList<BigInteger>();
    	keys.add(p); // index 0 - private Key p
    	keys.add(q); // index 1 - private Key q
    	keys.add(n); // index 2 - public Key n
    	keys.add(b); // index 3 - public Key B
    	return keys;
    }
	
    public static ArrayList<BigInteger> Encrypt(BigInteger x, BigInteger b, BigInteger n) {
    	BigInteger tmp = x.add(b);
    	BigInteger y = x.multiply(tmp);	
    	y = y.mod(n);
    	
    	if (b.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
    		b = b.divide(BigInteger.TWO);
    	} else {
    		b = b.add(n).divide(BigInteger.TWO);
    	}
    	
    	BigInteger c1 = x.add(b).mod(n).mod(BigInteger.TWO);
    	int c2 = iversonSymbol(jacobiSymbol(x.add(b), n));
    	
    	ArrayList<BigInteger> C = new ArrayList<BigInteger>();
    	C.add(y);						// index 0 - y
    	C.add(c1);						// index 1 - c1
    	C.add(BigInteger.valueOf(c2));	// index 2 - c2
    	return C;
    }
    
    public static ArrayList<BigInteger> findRoots(BigInteger y, BigInteger p, BigInteger q, BigInteger n) {
    	BigInteger s1 = y.modPow(p.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), p);
    	BigInteger s2 = y.modPow(q.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), q);
    	
    	BigInteger u = p.modInverse(q);
    	BigInteger v = BigInteger.ONE.subtract(u.multiply(p)).divide(q);
    	
    	BigInteger ups1 = u.multiply(p).multiply(s2);
    	BigInteger vqs2 = v.multiply(q).multiply(s1);
    	BigInteger x1 = ups1.add(vqs2).mod(n);
    	BigInteger x2 = ups1.subtract(vqs2).mod(n);
    	BigInteger x3 = vqs2.subtract(ups1).mod(n);
    	BigInteger x4 = BigInteger.ZERO.subtract(ups1).subtract(vqs2).mod(n);
    	
    	ArrayList<BigInteger> roots = new ArrayList<BigInteger>();
    	roots.add(x1);
    	roots.add(x2);
    	roots.add(x3);
    	roots.add(x4);
    	return roots;
    }
    
    public static BigInteger Decrypt(BigInteger y, BigInteger c1, BigInteger c2, BigInteger b, 
    		                         BigInteger n, BigInteger p, BigInteger q) {
    	BigInteger bDiv2;
    	if (b.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
    		bDiv2 = b.divide(BigInteger.TWO);
    	} else {
    		bDiv2 = b.add(n).divide(BigInteger.TWO);
    	}
    	
    	y = y.add(bDiv2.pow(2));
    	var roots = findRoots(y, p, q, n);
    	// subtract bDiv2 from each root
    	for (int i = 0; i < roots.size(); i++) {
    		BigInteger updatedX = roots.get(i).subtract(bDiv2);
    		roots.set(i, updatedX);
    	}
    	
    	// find right root
    	for (var x : roots) {
    		BigInteger x_c1 = x.add(bDiv2).mod(n).mod(BigInteger.TWO);
    		int x_c2 = iversonSymbol(jacobiSymbol(x.add(bDiv2), n));
    		
    		if (x_c1.equals(c1) && BigInteger.valueOf(x_c2).equals(c2)) {
    			return x;
    		}
    	}
    	
    	System.out.println("Decrypt: Could not find the root");
    	assert 1 == 0;
    	return null;
    }

    public static BigInteger Sign(BigInteger M, BigInteger b, BigInteger n, BigInteger p, BigInteger q) {
    	while (true) {
    		BigInteger x = formatMsg(M, n);
    		if (jacobiSymbol(x, p) == 1 && jacobiSymbol(x, q) == 1) {
    			var roots = findRoots(x, p, q, n);
    			int randomIndex = generateRandomNumber(4).intValue() % 4;
    			return roots.get(randomIndex);
    		}
    	}
    }

    public static boolean Verify(BigInteger M, BigInteger S, BigInteger n) {
    	BigInteger x = S.modPow(BigInteger.TWO, n);
    	System.out.println("Verify: x' = " + x.toString(16));
    	BigInteger restoredM = unformatMsg(x,  n);
    	System.out.println("Verify: restored M = " + restoredM.toString(16));
    	boolean result = restoredM.equals(M);
    	System.out.println("Verify: restored M == M : " + result); 
    	return result;
    }
    
    
	public static void main(String[] args) {
		System.out.println("main: Test Generate Keys");
		var keys = GenerateKeyPair(256);
		BigInteger privKey_p = keys.get(0);
		BigInteger privKey_q = keys.get(1);
		BigInteger pubKey_n = keys.get(2);
		BigInteger pubKey_b = keys.get(3);
		System.out.println("main: private key p = " + privKey_p.toString(16));
		System.out.println("main: private key q = " + privKey_q.toString(16));
		System.out.println("main: public  key n = " + pubKey_n.toString(16));
		System.out.println("main: public  key b = " + pubKey_b.toString(16));
		
		System.out.println("");
		System.out.println("main: Test Encrypt / Decrypt");
		BigInteger M = new BigInteger("abcd", 16);
		BigInteger formatedM = formatMsg(M, pubKey_n);
		
		System.out.println("main: original M = " + M.toString(16));
		System.out.println("main: original formatted M = " + formatedM.toString(16));
		var cParts = Encrypt(formatedM, pubKey_b, pubKey_n);
		BigInteger C_y = cParts.get(0);
		BigInteger C_c1 = cParts.get(1);
		BigInteger C_c2 = cParts.get(2);
		System.out.println("main: cyphertext y  = " + C_y.toString(16));
		System.out.println("main: cyphertext c1 = " + C_c1.toString(16));
		System.out.println("main: cyphertext c2 = " + C_c2.toString(16));
		
		BigInteger decM = Decrypt(C_y, C_c1, C_c2, pubKey_b, pubKey_n, privKey_p, privKey_q);
		BigInteger restoredM = unformatMsg(decM, pubKey_n);
		System.out.println("main: decrypted M = " + decM.toString(16));
		System.out.println("main: decrypted restored M = " + restoredM.toString(16));
		System.out.println("main: original M == decrypted unformatted M : " + M.equals(restoredM));
		
		System.out.println("");
		System.out.println("main: Test Sign / Verify");
		System.out.println("main: original M = " + M.toString(16));
		BigInteger S = Sign(M, pubKey_b, pubKey_n, privKey_p, privKey_q);
		System.out.println("main: signature M = " + S.toString(16));
		boolean result = Verify(M, S, pubKey_n);
		System.out.println("main: signature validation result = " + result);
		
		
		validateWithWebServer();
		znpAttack();
	}
	
	
	public static void validateWithWebServer() {
		System.out.println("");
		System.out.println("");
		System.out.println("Test with Web Server");
		
		//var A_keys = GenerateKeyPair(256);
		//BigInteger A_privKey_p = A_keys.get(0);
		//BigInteger A_privKey_q = A_keys.get(1);
		//BigInteger A_pubKey_n = A_keys.get(2);
		//BigInteger A_pubKey_b = A_keys.get(3);
		BigInteger A_privKey_p = new BigInteger("f5fc40ee9004aa2cc0186a06ff252267",16);
		BigInteger A_privKey_q = new BigInteger("f5d50a659b4777e7d211ba8b17dd4c7f",16);
		BigInteger A_pubKey_b = new BigInteger("f1f5819a078e0608ce89ab1a883da9e777fc2e7957ae72a9c2b6231803f",16);
    	BigInteger A_pubKey_n = A_privKey_p.multiply(A_privKey_q);    

		
		System.out.println("A: private key p = " + A_privKey_p.toString(16));
		System.out.println("A: private key q = " + A_privKey_q.toString(16));
		System.out.println("A: public  key n = " + A_pubKey_n.toString(16));
		System.out.println("A: public  key b = " + A_pubKey_b.toString(16));
		
		BigInteger WS_pubKey_n = new BigInteger("AA7ED4225A55B9E9B26D1FAA20DBB1A09218A03B4CB45AB6C596D3F9B05D8BBD", 16);
		BigInteger WS_pubKey_b = new BigInteger("913005921086DDD62BCC0B864E4B9CD9A1A1A6254E70398CED983F265B1DF8CF", 16);
		System.out.println("");
		System.out.println("Web Server: public key n = " + WS_pubKey_n.toString(16));
		System.out.println("Web Server: public key b = " + WS_pubKey_b.toString(16));
		
		System.out.println("");
		System.out.println("Encryption");
		BigInteger WS_C_y = new BigInteger ("9A5E930004F99AE5F83973FD7E0C391EBAE91316252A918681D6046C6B84D602",16);
		BigInteger WS_C_c1 = new BigInteger ("0",16);
		BigInteger WS_C_c2 = new BigInteger ("1",16);
		BigInteger decM = Decrypt(WS_C_y, WS_C_c1, WS_C_c2, A_pubKey_b, A_pubKey_n, A_privKey_p, A_privKey_q);
		BigInteger restoredM = unformatMsg(decM, A_pubKey_n);
		System.out.println("A: Message  = " + restoredM.toString(16));
		
		System.out.println("");
		System.out.println("Decryption");
		BigInteger MtoWS = new BigInteger("CAFED00D", 16);
		System.out.println("A: M = " + MtoWS.toString(16));
		BigInteger formatedMtoWS = formatMsg(MtoWS, A_pubKey_n);
		var C = Encrypt(formatedMtoWS, WS_pubKey_b, WS_pubKey_n);
		BigInteger C_y = C.get(0);
		BigInteger C_c1 = C.get(1);
		BigInteger C_c2 = C.get(2);
		System.out.println("A: cyphertext y  = " + C_y.toString(16));
		System.out.println("A: cyphertext c1 = " + C_c1.toString(16));
		System.out.println("A: cyphertext c2 = " + C_c2.toString(16));
				
		System.out.println("");
		System.out.println("Signature");
		BigInteger signatureFromWS = new BigInteger("5EF4177AE46977128BC4F8A50C19CF3248494878FFBC3975314B0F92269880EE", 16);
		System.out.println("Web Server: message : " + MtoWS.toString(16));
		System.out.println("Web Server: signature : " + signatureFromWS.toString(16));
		boolean result = Verify(MtoWS, signatureFromWS, WS_pubKey_n);
		System.out.println("A: validation signature from web server : " + result);
		
		System.out.println("");
		System.out.println("Verification Sign from A");
		BigInteger S = Sign(MtoWS, A_pubKey_b, A_pubKey_n, A_privKey_p, A_privKey_q);
		System.out.println("A: message = " + MtoWS.toString(16));
		System.out.println("A: signature of message = " + S.toString(16));

	}
	
	// Attack on Zero Knowledge Protocol
	public static void znpAttack() {
		Scanner in = new Scanner(System.in);
		System.out.println("");
		System.out.println("");
		System.out.println("Attack on Zero Knowledge Protocol");
		System.out.println("");
		System.out.print("Enter server key: ");
		String serverKeyStr = in.nextLine();
		BigInteger n = new BigInteger(serverKeyStr, 16);
		System.out.println("n (server key): " + n.toString(16));
		
		int count = 0;
		while (true) {
			count++;
			System.out.println("");
			System.out.println("Attempt #" + count);
			BigInteger t = generateRandomNumber(2048);
			BigInteger y = t.modPow(BigInteger.TWO, n);
			System.out.println("t = " + t.toString(16));
			System.out.println("y = " + y.toString(16));
			System.out.print("Enter z from server: ");
			String zStr = in.nextLine();
			BigInteger z = new BigInteger(zStr, 16);
			System.out.println("z = " + z.toString(16));
			
			if (!z.equals(t) && !z.equals(BigInteger.ZERO.subtract(t))) {
				BigInteger p = t.add(z).gcd(n);
				BigInteger q = n.divide(p);
				if (p.equals(BigInteger.ONE) || q.equals(BigInteger.ONE)) {
					System.out.println("p or q  == 1");
					System.out.println("Try one more time");
					continue;
				}
				System.out.println("Found p and q!");
				System.out.println("p = " + p.toString(16));
				System.out.println("q = " + q.toString(16));
				System.out.println("p*q = " + q.multiply(p).toString(16));
				System.out.println("n   = " + n.toString(16));
				break;
			} else {
				System.out.println("z == +-t");
				System.out.println("Try one more time");
			}
		}
	}

}
