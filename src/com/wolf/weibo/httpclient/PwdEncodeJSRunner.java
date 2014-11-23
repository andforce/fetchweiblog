
package com.wolf.weibo.httpclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.wolf.weibo.httpclient.javabean.PreLonginBean;

public class PwdEncodeJSRunner {

    Invocable inv = null;

    public PwdEncodeJSRunner() {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        
        File jsFile = new File("./js/ssologin.js");
        System.out.println("js 是否存在：    " + jsFile.exists());
        try {
            engine.eval(new FileReader(jsFile));
        } catch (FileNotFoundException e) {
        } catch (ScriptException e) {
        }
        if (engine instanceof Invocable) {
            inv = (Invocable) engine;
        }
    }

    public String getRsaPassWord(String p, PreLonginBean params) {
        System.out.println("inv == null? " + (inv == null));
        String pass = null;
        try {
        	System.out.println(p + "   " + params.getServertime() +"   "+ params.getNonce() + "   " + params.getPubkey());
            pass = inv.invokeFunction("getRsaPassWord", p, params.getServertime(), params.getNonce(), params.getPubkey()).toString();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        System.out.println("c = " + pass);
        return pass;
    }
}
