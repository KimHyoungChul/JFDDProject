package com.dlxxzx.tianditu;

import java.util.Random;

/**
 * 
 * ��˵��
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
         * ���ͼʸ����Ӱ��
         * 
         * 
         * 
         * if(this._mapStyle == "ImageBaseMap")//��ȡӰ���ͼ����ͼ��
                {
                    _baseURL = "http://t0.tianditu.com/img_c/wmts";

                else if(this._mapStyle == "ImageCNNote")//��ȡӰ���ͼ������ע�ǣ�
                {
                    _baseURL = "http://t0.tianditu.com/cia_c/wmts";
  
                else if(this._mapStyle == "ImageENNote")//��ȡӰ���ͼ��Ӣ��ע�ǣ�
                {
                    _baseURL = "http://t0.tianditu.com/eia_c/wmts";
                }
                else if(this._mapStyle == "TerrainBaseMap")//��ȡ����ͼ����ͼ��
                {
                    _baseURL = "http://t0.tianditu.com/ter_c/wmts";

                }
                else if(this._mapStyle == "TerrainCNNote")//��ȡ����ͼ������ע�ǣ�
                {
                    _baseURL = "http://t0.tianditu.com/cta_c/wmts";

                }
                else if(this._mapStyle == "TerrainENNote")//��ȡ����ͼ��Ӣ��ע�ǣ�
                {
                }
                else if(this._mapStyle == "VectorBaseMap")//��ȡʸ��ͼ����ͼ��
                {
                    _baseURL = "http://t0.tianditu.com/vec_c/wmts";
                }
                else if(this._mapStyle == "VectorCNNote")//��ȡʸ��ͼ������ע�ǣ�
                {
                    _baseURL = "http://t0.tianditu.com/cva_c/wmts";

                }
                else if(this._mapStyle == "VectorENNote")//��ȡʸ��ͼ��Ӣ��ע�ǣ�
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
