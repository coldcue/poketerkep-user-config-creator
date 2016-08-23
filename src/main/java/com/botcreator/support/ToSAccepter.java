package com.botcreator.support;


import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.player.PlayerProfile;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import okhttp3.OkHttpClient;

import java.net.Proxy;

public class ToSAccepter {
    public static void acceptTos(String username, Proxy proxy) throws LoginFailedException, RemoteServerException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .proxy(proxy)
                .build();

        String password = UserNameGenerator.getPassword(username);

        PtcCredentialProvider ptcCredentialProvider = new PtcCredentialProvider(okHttpClient, username, password);
        PokemonGo pokemonGo = new PokemonGo(ptcCredentialProvider, okHttpClient);

        PlayerProfile playerProfile = pokemonGo.getPlayerProfile();
        playerProfile.enableAccount();
        playerProfile.updateProfile();
    }
}
