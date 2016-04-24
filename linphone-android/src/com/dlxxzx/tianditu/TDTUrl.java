package com.dlxxzx.tianditu;

import java.util.Random;

/**
 * 
 * 类说明
 */
public class TDTUrl  {
    private TianDiTuTiledMapServiceType _tiandituMapServiceType;
    private int _level;
    private int _col;
    private int _row;
    public TDTUrl(int level, int col, int row,TianDiTuTiledMapServiceType tiandituMapServiceType){
        this._level=level;
        this._col=col;
        this._row=row;
        this._tiandituMapServiceType=tiandituMapServiceType;
    }
    public String generatUrl(){
        /**
         * 天地图矢量、影像
         * 
         * 
         * 
         * if(this._mapStyle == "ImageBaseMap")//获取影像地图（底图）
                {
                    _baseURL = "http://t0.tianditu.com/img_c/wmts";

                else if(this._mapStyle == "ImageCNNote")//获取影像地图（中文注记）
                {
                    _baseURL = "http://t0.tianditu.com/cia_c/wmts";
  
                else if(this._mapStyle == "ImageENNote")//获取影像地图（英文注记）
                {
                    _baseURL = "http://t0.tianditu.com/eia_c/wmts";
                }
                else if(this._mapStyle == "TerrainBaseMap")//获取地形图（底图）
                {
                    _baseURL = "http://t0.tianditu.com/ter_c/wmts";

                }
                else if(this._mapStyle == "TerrainCNNote")//获取地形图（中文注记）
                {
                    _baseURL = "http://t0.tianditu.com/cta_c/wmts";

                }
                else if(this._mapStyle == "TerrainENNote")//获取地形图（英文注记）
                {
                }
                else if(this._mapStyle == "VectorBaseMap")//获取矢量图（底图）
                {
                    _baseURL = "http://t0.tianditu.com/vec_c/wmts";
                }
                else if(this._mapStyle == "VectorCNNote")//获取矢量图（中文注记）
                {
                    _baseURL = "http://t0.tianditu.com/cva_c/wmts";

                }
                else if(this._mapStyle == "VectorENNote")//获取矢量图（英文注记）
                {
                    _baseURL = "http://t0.tianditu.com/eva_c/wmts";
                }
         * 
         * 
         * */
        StringBuilder url=new StringBuilder("http://t");
        Random random=new Random();
        int subdomain = (random.nextInt(6) + 1);
        url.append(subdomain);
        switch(this._tiandituMapServiceType){
        case VEC_C:
             url.append(".tianditu.com/DataServer?T=vec_c&X=").append(this._col).append("&Y=").append(this._row).append("&L=").append(this._level);
             //url.append(".tianditu.com/vec_c/wmts?request=GetTile&service=wmts&version=1.0.0&layer=vec&style=default&format=tiles&TileMatrixSet=c&TILECOL=").append(this._col).append("&TILEROW=").append(this._row).append("&TILEMATRIX=").append(this._level);
             //url.append(".tianditu.com/DataServer?T=vec_w&X=").append(this._col).append("&Y=").append(this._row).append("&L=").append(this._level); 
            break;
        case CVA_C:
        	 url.append(".tianditu.com/DataServer?T=cva_c&X=").append(this._col).append("&Y=").append(this._row).append("&L=").append(this._level);
        	 //url.append(".tianditu.com/DataServer?T=cva_w&X=").append(this._col).append("&Y=").append(this._row).append("&L=").append(this._level);
            break;
        case CIA_C:
        	 url.append(".tianditu.com/DataServer?T=cia_c&X=").append(this._col).append("&Y=").append(this._row).append("&L=").append(this._level);
            break;
        case IMG_C:
        	 url.append(".tianditu.com/DataServer?T=img_c&X=").append(this._col).append("&Y=").append(this._row).append("&L=").append(this._level);
            break;
            default:
                return null;
        }
        return url.toString();
    }
   
}
