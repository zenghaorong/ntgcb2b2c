package com.aebiz.app.alipay.modules.models;

/**
 * @Auther: zenghaorong
 * @Date: 2019/4/19  10:20
 * @Description: 支付宝配置类
 */
public class AlipayConfig {


    /***********************************测试环境使用信息************************************************/
    // 商户appid
//    public static String APPID = "2019042264282233";
//    // 私钥 pkcs8格式的
//    public static String RSA_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCwjtJWFn0IUYNNv8QobLFQx3Mi04fISDEqnrlagSofy/er4CXbHwcfurf1gXmJ9umVmgQb61Ntlv0j3rWJUowoIndnLTH5TWuYNzcXP9tLKUjQUf8FgGFW7K9MErGajA18+754ukPH1au7pfx8OrUU3Vyy4FpNiNeK8mAgl0YFMjHRcmA8E80TSglD4wU6tSHsQL+Nel/QmMGdKqzzo9oSt/KXm2n4hnjOmp31j40alIuuPn6S2ilskmK3NFCkJ5Mz4rFiG72j8q6cXrtg0VymaPca6jh0WZtir96ztm1c/FlvvsXNJ8Kwi1eEmm8k88Ntmd+2NvSVLduWXfI3UlkzAgMBAAECggEBAI4PnPZfyeLzk27vqenM6y9QDDgtc8eXLthYNlO4Mii7eWD2Tf9ti9Hf7zy/HQgBgBk7f6rnQo2fO7es7uy7WsLAWMrP9rHdtYEYjF9Mpipn4YqdL1FB5xCGCNZxBC6SGhhChxOk6/zS1Ad8mxdqNWHAa2ISWxaf0K6gLvCKR3DNhxi8iKbUI0osJCpKU/6nREJdE0SuTlMele+/8THvzNUYkCsTCzOd3QcbUaEeqCsVoWZKvjXS/IuNd87hO9o6nDt5zrwXRmDgoPPabneksdNhF3rgyj5DTxxdte0O8AZKypQhCNofGH7LjdxEPOB6WM6dvxP1CaQ7MNFtHisSVIECgYEA58dT5yEoHrQb4zhVOsHZV7RQ0nAReLgIZPMS+MLqCLh4T0rhB8mlh0j5Q3/RnVs/URsxctBNIsF9BGIgEAN6himBeshfe9/y2kc1dT4JKOfm9Tx3eAbKBJZAGeux+9YjNMMWhiyx0Jk9Dwca0Ci4UFk3zd8UgqCp7Dz4Rw+htHECgYEAwwIyqXsZTi20G8yuwNDtVJwEGDdnc3zvoE8HLHtKLyk/cAQa4nigbjnMMB/etZ43ArTb0QPP6a7SUWXLNFuDXa1kVDmBNhgWlF1DDRKrGvwSN8umPsqqzsj05NQcdZ5A+N7baadL5BSvGlUO3ntVQEZk1p+saLSffEAGzxk9aeMCgYAF5xCe8Tw5U/Ll3XhmL6ueTSxFv67iOSWVlI8mIKifIjuGIW0Lqrn5cQVQD8BB1qINKbkfqGheezj41JrmbYBATGxMuS+dUv23S1r0KmlHTmiWVDzipKGebkkhn9v/gtuQq2s2bYr1ugFREahAJtlSyoLeTlESZo8NQhw8iwxeMQKBgQDB3O2Aj2AY2Fa1TEZuaUWHV5K6gFnX9dxDAk2favHU8KygL3SKsGDNM6hKL2S4KRGHH0VoBOIs7h8nzIq3AkMWXcnf8UsC++92j0CZPnXb3bw+u7YJtnEYIa9TzYp9Y1Y0E1kNL8PCes2Y3/ZoNzQJMfINa696aBYk74HVYMItAQKBgD/AVbDJoT9F7+xzB0pSB7Xm1Met7wjLHdgjiWuG38okpi7gI+cM50JIfWlmJWW2gN/zTUZUlndSLIOnO/vUPXNwFHv/EGPgBfJoAHtEXpKDWeZ3Q4AgQCXZnb3oOpxwZEwXBblg6OX9IfDZfaRweWTSjIXKBgMySfAHxOCEpnCZ";
//    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
////    public static String notify_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/notify_url.jsp";
//    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
////    public static String return_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/return_url.jsp";
//    // 请求网关地址
//    public static String URL = "https://openapi.alipay.com/gateway.do";
//    // 编码
//    public static String CHARSET = "UTF-8";
//    // 返回格式
//    public static String FORMAT = "json";
//    // 支付宝公钥
//    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlWTmNXHNe+vdLbXifyU7BH0TcQ+vwS7XrW+lpyWw7LP/mzfzPRqj80efKBPHLf7sUgct5a6xjl0GhWSiMmhpVj3NvehhWm/CVleu5/lClIccnmVTDpXPClYmufH7FnSn5o4+1oT360kcRmWaiAoNjkIzwteJ9cIZdQ8Giu+ImIm3aS5cBQWna4V/2HfPfg+U6JjXzEhtQtrJjzyMXQ4hKzJd8z0MoW8iyRiOD56YG/OKmsuyrpSDMRfeEbUYnin9FIj+e0AR8oDcYdhyHh8KU8QQa417rqgCNm/yysY5XQ4HvEd7npMF6Bt2eL+2wCpFfWdy1+esDKHamZsXmgy2lwIDAQAB";
//    // 日志记录目录
//    public static String log_path = "/log";
//    // RSA2
//    public static String SIGNTYPE = "RSA2";



    /***********************************正式环境使用信息************************************************/
    // 商户appid
    public static String APPID = "2019042264300093";
    // 私钥 pkcs8格式的
    public static String RSA_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC883aF430k8+LFu//hV3HZvrvw9GCHORRZ6TcB8J1oqGJNh+GlZuhSrnAu1jnOWN+Chup79ALkg0nlWI56YKLzJJaxZAB7fQBzF5neJzFNz8RH4pIYzbezZeZWgqBYLXCctervlHXv39UmppN5KRFBT1BqemKZmI3V3fZKj6c16WL95ifN1mZWH+SZ7ikb5lbnmAO+HnzBoiGnfX7px/p19IzCgfX7s05X0oIQmnUtsJy/1Dnsv2oGPjiWm1IlCZzI7coX5/r3hUAqgX7iqjopXlpW1jDl0T5MjdIKwlGqJbJ5uOA3KFm2cVv4J9xa6F9DrPwbnuZc2yM7+E+lIDcrAgMBAAECggEAZKfNm8V0BRtm0q8zwHc0zyosNE/6nA5sKPlztDTZpccNlv+r1NDdMi561HiKksIFTyNQBngWkmTeGK70N30DUUNMg3MiTqZoduc7sHxwZ2MmzKSF73CnS4odCjjL8i1CsDi4zqfnq8Exa0WRihm2d3QpAo/vtqLt6+AyM8YS7URcKjsjZTJ7XarnPGMmy77IENQxA5NC4FEl6pfn3Fjmi4KlsiNNUvhOCXPRko6rahdGiDLkR6mDXZy0PDiB8C+ugvAtB2o0n3qL+0fMDhmvZO4U0sAcLPloUHPgry1xU8P0MdV4hQCp2z72GdGz4/i0b0VprtNpLvawJ1FHiLnnAQKBgQDoUyaRJV9khgU5FHlRs1Ae9hsNhTyoFSPSL4+qGMirfJ0yJcfw33nUaZmSCGEGHTK+esrDFPMTTdft3TuIJVGI/M2zL5Ha/nNoRizwHJqpM608R/edHvjk9TxhviXYqjM/w4fPY+GfIcyYs4ilbBrz6NaKEWfOtbeSTwlZj2DsvQKBgQDQNMjf3ZHAsn4f2lKPQzysNryJpBWj9VJivaKb4h728vA3u2ZBQFGjNy8ecgxlfzmy3SLzAt9DqWGz5d0ap1zPJNTipJZyf2lIXMs8QIEnYPGw8OpvT3xyJDFmqAUfHv/DvWUU2tXBLkXWj+LtVE/wV1zzLptYZehxBzQSwX+WBwKBgQDln6HD3iNt0MUCyMisRsXPBEJy15i/LL3N9fJSFdLpI+6e7Ra0bb0VhBYkbyGukFdiSy1zKZajItJkKMC5bOEsu2l4THEl+U8J5ipuXGdFC22S4jLL2hv4+KZomS8A+iBTSqhGBBuxVA/Lcc+CaNou75h0uIVYmuTIuFOucHdFJQKBgAfL0jG/xDNo+5wJusmuhNkV+51TzZOkHf+ghjQ8FVi95AzSMZQD6oqoCFWlg51RVEHEUgizdNS9xPDNjKfw2GLRSD9sVk3XQ4P4JYwOBkgJM0Oi4cRlP42umUL0y6xMptsXfm93ekWPjAHqxV6Jp6yiONLKTfrTZg7FbEe/gx2FAoGBAIISNOz4srVvAx/+ze9JaF7g1s+mei/9riaa4qabNbU9KxUBwt6Qjfkt9sraxf54AM5zYA7YLovpmEIJavZ3jw+iJlY3KSq553m1eNJK5KFC4P9afBDS4bc3crvqbMF/c0+p7tX763J1WjOYMiOeDpj73sMSqjhhTXowbKUu8ARC";
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
//    public static String notify_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/notify_url.jsp";
    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
//    public static String return_url = "http://商户网关地址/alipay.trade.wap.pay-JAVA-UTF-8/return_url.jsp";
    // 请求网关地址
    public static String URL = "https://openapi.alipay.com/gateway.do";
    // 编码
    public static String CHARSET = "UTF-8";
    // 返回格式
    public static String FORMAT = "json";
    // 支付宝公钥
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlWTmNXHNe+vdLbXifyU7BH0TcQ+vwS7XrW+lpyWw7LP/mzfzPRqj80efKBPHLf7sUgct5a6xjl0GhWSiMmhpVj3NvehhWm/CVleu5/lClIccnmVTDpXPClYmufH7FnSn5o4+1oT360kcRmWaiAoNjkIzwteJ9cIZdQ8Giu+ImIm3aS5cBQWna4V/2HfPfg+U6JjXzEhtQtrJjzyMXQ4hKzJd8z0MoW8iyRiOD56YG/OKmsuyrpSDMRfeEbUYnin9FIj+e0AR8oDcYdhyHh8KU8QQa417rqgCNm/yysY5XQ4HvEd7npMF6Bt2eL+2wCpFfWdy1+esDKHamZsXmgy2lwIDAQAB";
    // 日志记录目录
    public static String log_path = "/log";
    // RSA2
    public static String SIGNTYPE = "RSA2";



}
