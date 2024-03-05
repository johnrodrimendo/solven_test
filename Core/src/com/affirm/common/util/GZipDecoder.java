package com.affirm.common.util;

public class GZipDecoder {
    public static void main(String[] args) {
        String str = "H4sIAAAAAAAAAI1Vy4rbMBT9F6+TQXaScca7oaVQKMxm6KYUcSPLjooeRo807jD/3ms7fsUeWsgiOUe6OvfcR368RdKApqYouKUij7LdIU0Oh00HQ1VJwcALo1syfkyOu91xE1Xcuhv2lJL0Kd1EoEzQPsoO8f7hiL+Fdh6kVFx7F2U6SDnDaH++YzgKYF5cOAWtA0hqwfMlqYz2Z1nP2FYpM0oJ51BoDyuh795QcF1B1nRaXgrn0ZG8fSdKSJJsY7IlyWsSZ/s0S+KHwzF9Ou625JAREm0iJ0oNPlhOCwxBNahB4EjlE9kT1LDQWuLrireefnhCB3XidnlAgZBLt3JE515ZxtCqnCruZvZ19UemyfbbC0kw2UMS79MtiTE54ajjEqNyFOdt4JtoafiIUFFeetQbdHelPJiN8HWbbfI4U2Fs3qQYz56lp5oyKXhTu07AHQkaZO2QLUA6pDuuEeNqF9ytu2++Ddy0IMvmpHApP2xQZpyfeetObujPSax/0IhZX0I5hIG8d7GzTmg0j6+YHK9gydDZBqUZK4L6H42UF9eVaE0/TtwJFjQbgMqaPDC82hVyWmHyQMh4oF0ce3RQVdLUs0KwYC3XrGsD0o8yVAKFjZskN781raCeujnF6CzMrL0cM3b0DixdpDEA+K5kQXbLbpp5U+g+p0JYrHoe5qPcoSchpdAltqlxc/4s8pzrFYs+birqjLzwcVqu7Ay65PO12OXY+1yBxaXjbwYnjylJNpE2uAcx8MpWxeqrinq49mrwKz2BW9lQldCLW3Mp10pYmIzUuDJ3r4Rk7WdlV2JgegEpmksre6+hvRXjshon95b83WT/ckM/BueNatRAlL1FIAuhP72csNR/4Lso8RT/fONwTMrGpOe7f7Cvr196iLy/b6JuPTl25nmQt9Tff/4F878XP0MHAAA=";
        System.out.println(GzipUtil.unzip(GzipUtil.decode(str)));
    }
}