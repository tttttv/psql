package com.psql;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerApp {
    public static void main(String[] args){
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(workerGroup) //Группы
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { //Обработка подключения
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception{
                            socketChannel.pipeline().addLast(new MainHandler());
                        }
                    });
            ChannelFuture future = b.bind(8002).sync(); //Запуск сервера
            System.out.println("Server started");
            future.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
