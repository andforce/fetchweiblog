
package com.wolf.weibo.httpclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class PwdEncodeJSRunner {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("JavaScript");
    Invocable inv = null;

    public PwdEncodeJSRunner() {
        File jsFile = new File("./sso.js");
        System.out.println("js 是否存在：    " + jsFile.exists());
        try {
            engine.eval(new FileReader(jsFile));
        } catch (FileNotFoundException e) {
        } catch (ScriptException e) {
        }
        inv = (Invocable) engine;
    }

    public String getPwd(String p, HashMap<String, String> params, String nonce) {
        System.out.println("inv == null? " + (inv == null));
        String pass = null;
        try {
            pass = inv.invokeFunction("getpass", p, params.get("servertime"), nonce, params.get("pubkey")).toString();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        System.out.println("c = " + pass);
        return pass;
    }
}
