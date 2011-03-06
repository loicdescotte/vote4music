/**
 * @author Nabil Adouani
 */

(function(){
    
    Ext.namespace('App.vote4music');
    
    var genres = [['ROCK', 'Rock'], ['METAL', 'Metal'], ['JAZZ', 'Jazz'], ['BLUES', 'Blues'], ['POP', 'Pop'], ['WORLD', 'World'], ['HIP_HOP', 'Hip Hop'], ['OTHER', 'Other']];
    
    var $cls = App.vote4music.ComboBox = function(cfg){
	    $cls.superclass.constructor.call(this, Ext.apply({
	    	typeAhead: true,
	        triggerAction: 'all',
	        lazyRender:true,
	        mode: 'local',
	        
	    	store: new Ext.data.ArrayStore({
	    		//url: '/genres.json',
	    		fields: ['name', 'label'],
	    		autoLoad: true,
	    		data: genres
	    	}),	 
	    	
	    	valueField: 'name',
	    	displayField: 'label'
	    },cfg));
    };
    
    Ext.extend($cls, Ext.form.ComboBox, {
    	
    });
})();

(function(){
    
    Ext.namespace('App.vote4music');
    
    var $cls = App.vote4music.ArtistPanel = function(cfg){
	    $cls.superclass.constructor.call(this, Ext.apply({
	    	title: 'Album Artists',
	    	layout: 'fit',
	    	flex: 1,
	    	html: 'artists'
	    },cfg));
    };
    
    Ext.extend($cls, Ext.Panel, {
    	
    });
})();


(function(){
    
    Ext.namespace('App.vote4music');
    
    var $cls = App.vote4music.MainPanel = function(cfg){
    	this.tplAlbums = new Ext.XTemplate(
			'<tpl for=".">',
	            '<div class="thumb-wrap" id="{name}">',
	            	'<div class="thumb"><img src="/public/shared/covers/{id}" title="{name}"></div>',
	            	'<div><span class="artistName">{artist}</span></div>',
	            	'<div><span class="albumTitle">{shortName}</span></div>',
			    '</div>',
	        '</tpl>',
	        '<div class="x-clear"></div>'
		).compile();
	    
	    this.detailsTemplate = new Ext.XTemplate(
			'<div class="details">',
				'<tpl for=".">',
					'<img src="/public/shared/covers/{id}" style="width:180px;"><div class="details-info">',
					'<b>Album Name:</b>',
					'<span>{name}</span>',
					'<b>Artist:</b>',
					'<span>{artist}</span>',
					'<b>Release Date:</b>',
					'<span>{releaseDate}</span>',
					'<b># Votes:</b>',
					'<span>{nbVotes}</span></div>',
				'</tpl>',
			'</div>'
		).compile();
	    
	    this.lookup = {};
    	
	    var formatData = function(data){
	    	data.shortName = Ext.util.Format.ellipsis(data.name, 15);
	    	this.lookup[data.name] = data;
	    	return data;
	    };
    	
	    $cls.superclass.constructor.call(this, Ext.apply({
	    	id: 'albums-view',
	    	width: 800,
	    	height: 500,
	    	
	    	title: 'Vote4Music - ExtJS UI',
	    	layout: 'border',
	    	items: [
	    	    new Ext.Panel({
	    	    	region: 'center',
	    	    	header: false,
	    	        layout:'fit',
	    	        
	    	        margins: '5 0 5 5',
	    	        
	    	    	items: [
						this.albumsPanel = new Ext.DataView({							
							region : 'center',
							border: true, 
							store: new Ext.data.JsonStore({
								url: '/api/albums.json',
								fields: [
								    'id',
								    'name',
								    {name: 'artist', mapping: 'artist.name'},
								    {name: 'releaseDate'},
								    'nbVotes',
								    {name: 'hasCover', type: 'boolean'}
								],
								autoLoad: true,
								listeners: {
							    	'load': {fn:function(){ this.albumsPanel.select(0); }, scope:this, single:true}
							    }
							}),
							tpl: this.tplAlbums,
							
							singleSelect: true,
							overClass:'x-view-over',
							itemSelector: 'div.thumb-wrap',
							emptyText : '<div style="padding:10px;">No albums found</div>',
							
							prepareData: formatData.createDelegate(this),
				            listeners: {
								'selectionchange': {fn:this.showDetails, scope:this, buffer:100}
							},
						})        
	    	    	]/*,	    	    	
	    	    	tbar: [
	    	    	     new App.vote4music.ComboBox({})  
	    	    	]
	    	    	*/
	    	    
	    	    }),
	    	    this.previewPanel= new Ext.Panel({
	    	    	region: 'east',
	    	    	width: 250,
	    	    	header: false,
	    	    	margins: '5'
	    	    })
	    	]
	    },cfg));
    };
    
    Ext.extend($cls, Ext.Panel, {
    	showDetails : function(){
		    var selNode = this.albumsPanel.getSelectedNodes();
		    var detailEl = this.previewPanel.body;
			if(selNode && selNode.length > 0){
				selNode = selNode[0];
			    var data = this.lookup[selNode.id];
	            detailEl.hide();
	            this.detailsTemplate.overwrite(detailEl, data);
	            detailEl.slideIn('l', {stopFx:true,duration:.2});
			}else{
			    detailEl.update('');
			}
		}
    });
})();

Ext.onReady(function() {
    Ext.QuickTips.init();
    
    /*
    var p = new Ext.Container({
    	renderTo: Ext.getBody(),
    	layout: 'ux.center',
    	bodyStyle: 'padding:10px 10px;',
    	width: '100%',
    	//border: false,
    	items:[
    	       new App.vote4music.MainPanel({})
    	]
    })
    */
    
    new App.vote4music.MainPanel({
    	renderTo: Ext.getBody()
    });
});
