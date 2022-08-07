package com.psql;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MainHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Подключение");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        while (buf.readableBytes() > 0){
            System.out.print((char) buf.readByte());
        }
        buf.release(); //Освобождает память, иначе буффер не освободится

        ByteBuf in = (ByteBuf) msg;

        String st = "OK";
        ctx.writeAndFlush(Unpooled.copiedBuffer(st, CharsetUtil.UTF_8));
        ctx.close();

        String url = "jdbc:postgresql://localhost:5432/avilon";
        String user = "avilon";
        String password = "123456";

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("INSERT INTO locations (imei, test) VALUES ('1234567989', 4)")) {

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Отключение");
    }
}
