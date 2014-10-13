package com.letv.datastatistics.util;

public class LetvBase64 {
	
	public static String encode(byte b[]) {
		  int code = 0;
		  StringBuffer sb = new StringBuffer((b.length - 1) / 3 << 6);
		  for (int i = 0; i < b.length; i++) {
		   code |= b[i] << 16 - (i % 3) * 8 & 255 << 16 - (i % 3) * 8;
		   if (i % 3 == 2 || i == b.length - 1) {
		    sb.append(_$6698[(code & 0xfc0000) >>> 18]);
		    sb.append(_$6698[(code & 0x3f000) >>> 12]);
		    sb.append(_$6698[(code & 0xfc0) >>> 6]);
		    sb.append(_$6698[code & 0x3f]);
		    code = 0;
		   }
		  }

		  if (b.length % 3 > 0) {
		   sb.setCharAt(sb.length() - 1, '=');
		  }
		  if (b.length % 3 == 1) {
		   sb.setCharAt(sb.length() - 2, '=');
		  }
		  return sb.toString();
		 }

		 public static byte[] decode(String code) {
		  if (code == null) {
		   return null;
		  }
		  int len = code.length();
		  if (len % 4 != 0) {
		   throw new IllegalArgumentException(
		     "Base64 string length must be 4*n");
		  }
		  if (code.length() == 0) {
		   return new byte[0];
		  }
		  int pad = 0;
		  if (code.charAt(len - 1) == '=') {
		   pad++;
		  }
		  if (code.charAt(len - 2) == '=') {
		   pad++;
		  }
		  int retLen = (len / 4) * 3 - pad;
		  byte ret[] = new byte[retLen];
		  for (int i = 0; i < len; i += 4) {
		   int j = (i / 4) * 3;
		   char ch1 = code.charAt(i);
		   char ch2 = code.charAt(i + 1);
		   char ch3 = code.charAt(i + 2);
		   char ch4 = code.charAt(i + 3);
		   int tmp = _$6699[ch1] << 18 | _$6699[ch2] << 12 | _$6699[ch3] << 6
		     | _$6699[ch4];
		   ret[j] = (byte) ((tmp & 0xff0000) >> 16);
		   if (i < len - 4) {
		    ret[j + 1] = (byte) ((tmp & 0xff00) >> 8);
		    ret[j + 2] = (byte) (tmp & 0xff);
		    continue;
		   }
		   if (j + 1 < retLen) {
		    ret[j + 1] = (byte) ((tmp & 0xff00) >> 8);
		   }
		   if (j + 2 < retLen) {
		    ret[j + 2] = (byte) (tmp & 0xff);
		   }
		  }

		  return ret;
		 }
	
		 
		 private static char _$6698[] = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			   'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			   'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			   'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			   'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
			   '8', '9', '+', '/' };
		 private static byte _$6699[] = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62,
			   -1, 63, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1,
			   0, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
			   15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1,
			   26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
			   43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };
		 
		 
//		 http://119.167.223.131/gate_way.m3u8?ID=id9,id10&id9.proxy_url=aHR0cDovLzEyMy4xMjUuODkuNDgvMjEvMzUvOTQvbGV0di11dHMvMjQ4MjQyNi1BVkMtMjQ4NDY0LUFBQy02NDA3MC0zMjMzMy0xMjc0NDYxLTI5YWZmN2I5YmMzN2IzZDZmYTdjZTZjYTQyMTQyMmZkLTEzNjY0NDY5MDQ3NzAubXA0P2NyeXB0PTVjZmVmMjRlYWE3ZjJlNzEmYj0zMTQmZ249NzA2Jm5jPTEmYmY9MTgmcDJwPTEmdmlkZW9fdHlwZT1tcDQmY2hlY2s9MCZ0bT0xMzY3MzIzMjAwJmtleT01MTQ0NmE3ODg0YWZiZmQwNTBlMDY5MDNmYmFjNjdiMCZvcGNrPTEmbGduPWxldHYmcHJveHk9MjA3MTgxMjQ0MyZjaXBpPTE2ODQ2ODY5MyZnZW89Q04tMS05LTImdHNucD0xJm1tc2lkPTIzNjI3MTEmcGxhdGlkPTMmc3BsYXRpZD0wJm0zdTg9aW9zJnNpZ249bWImZG5hbWU9bW9iaWxlJnRhZz1pb3MmdnR5cGU9cGxheQ==&id10.proxy_url=aHR0cDovL2czLmxldHYuY24vdm9kL3YxL01qSXZNak12TkRrdmJHVjBkaTEzWldJdk1qQXhNekEwTURndk1qYzFOVGczTlRjNEwzQmhaR0o1Ym5Cd0xtMXdOQT09P2I9MTIzNCZsZXR2RXh0aWQ9OSZ0YWc9Z3VnJm5wPTEmZ3VndHlwZT01Jm0zdTg9aW9z
//  
//	     http://119.167.223.131/gate_way.m3u8?ID=id9,id10&id9.proxy_url=aHR0cDovLzEyMy4xMjUuODkuNTIvMjIvMjMvNDkvbGV0di13ZWIvMjAxMzA0MDgvMjc1NTg3NTc4L3BhZGJ5bnBwLmxldHY/Y3J5cHQ9NDBhN2M1Y2FhYTdmMmUyNjgmYj0xMjM0JmduPTcwNiZuYz0xJmJmPTE3JnAycD0xJnZpZGVvX3R5cGU9bXA0JmNoZWNrPTAmdG09MTM2NjQ0NDgwMCZrZXk9Y2MwMDRhNTNlNDQ1MzE1MTU2NDM0MzE0MTQ4MjJmYmEmb3Bjaz0xJmxnbj1sZXR2JnByb3h5PTIwNzE4MTI0NDQmY2lwaT0xNjg0NjkyMDQmZ2VvPUNOLTEtOS0yJnRzbnA9MSZsZXR2RXh0aWQ9OSZ0YWc9Z3VnJm5wPTEmZ3VndHlwZT01Jm0zdTg9aW9z&id10.proxy_url=aHR0cDovLzEyMy4xMjUuODkuNTUvMzAvNTEvMzMvbGV0di11dHMvMjQ1OTIxMC1BVkMtNTQ3NjE4LUFBQy0zMjAwMC0yNjE0MjQwLTE5MDUxNjUwNS0yY2Q2MjNiMmIzZjUwMDUwM2E0N2ZkZWE4Nzg0NmRkOS0xMzY2MjA0MTk5NzIxLm1wND9jcnlwdD0xNmU0ZTc3NmFhN2YyZTE0OSZiPTU4MiZnbj03MDYmbmM9MSZiZj0yMSZwMnA9MSZ2aWRlb190eXBlPW1wNCZjaGVjaz0wJnRtPTEzNjcxMjg4MDAma2V5PTEzMDk2ZGUzMDJhMWZiODc4YTU1MGUwNzk3ZWQ3YmZlJm9wY2s9MSZsZ249bGV0diZwcm94eT0yMDcxODEyNDUyJmNpcGk9MTY4NDY5MjA0Jmdlbz1DTi0xLTktMiZ0c25wPTEmbW1zaWQ9MjM1OTAwMiZwbGF0aWQ9MyZzcGxhdGlkPTAmbTN1OD1pb3Mmc2lnbj1tYiZkbmFtZT1tb2JpbGUmdGFnPWlvcyZ2dHlwZT1wbGF5JnBjb2RlPTAxMDUxMDAwMCZ2ZXJzaW9uPTMuNQ==

			 

}
