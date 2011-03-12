/**
 * @author Nabil Adouani
 */

(function(){
    
    Ext.namespace('App.vote4music');
    
    var genres = [['', ''], ['ROCK', 'Rock'], ['METAL', 'Metal'], ['JAZZ', 'Jazz'], ['BLUES', 'Blues'], ['POP', 'Pop'], ['WORLD', 'World'], ['HIP_HOP', 'Hip Hop'], ['OTHER', 'Other']];
    
    var $cls = App.vote4music.ComboBox = function(cfg){
	    $cls.superclass.constructor.call(this, Ext.apply({
	    	editable: false,
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
	    	displayField: 'label',
	    	tpl: '<tpl for="."><div class="x-combo-list-item">&nbsp;{label}</div></tpl>'
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
	            '<div class="thumb-wrap" id="album_{id}">',
	            	'<tpl if="hasCover == true">',
	            		'<div class="thumb"><img src="/public/shared/covers/{id}" title="{name}"></div>',
	            	'</tpl>',
	            	'<tpl if="hasCover == false">',
            			'<div class="thumb"><img src="/public/images/unknown.png" title="{name}"></div>',
            		'</tpl>',
	            	'<div><span class="artistName">{artist}</span></div>',
	            	'<div><span class="albumTitle">{shortName}</span></div>',
			    '</div>',
	        '</tpl>',
	        '<div class="x-clear"></div>'
		).compile();
	    
	    this.detailsTemplate = new Ext.XTemplate(
			'<div class="details">',
				'<tpl for=".">',
					'<tpl if="hasCover == true">',
		        		'<img src="/public/shared/covers/{id}" style="width:180px;" title="{name}">',
		        	'</tpl>',
		        	'<tpl if="hasCover == false">',
		    			'<img src="/public/images/unknown.png" style="width:180px;" title="{name}">',
		    		'</tpl>',
					'<div class="details-info">',
					'<b>Album Name:</b>',
					'<span>{name}</span>',
					'<b>Artist:</b>',
					'<span>{artist}</span>',
					'<b>Release Date:</b>',
					'<span>{releaseDate}</span>',
					'<b># Votes:</b>',
					'<span>{nbVotes}</span></div>',
					'<div id="voteLink" class="voteLink">Vote for It!</div>',
				'</tpl>',
			'</div>'
		).compile();
	    
	    this.lookup = {};
    	
	    var formatData = function(data){
	    	data.shortName = Ext.util.Format.ellipsis(data.name, 15);
	    	this.lookup['album_' + data.id] = data;
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
						this.albumsView = new Ext.DataView({							
							region : 'center',
							border: true, 
							store: new Ext.data.JsonStore({
								proxy : new Ext.data.HttpProxy({
				                     method: 'GET',
				                     url: '/api/albums/json'
				                }),
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
							    	'load': {fn:function(){ this.albumsView.select(0); }, scope:this}
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
	    	    	],	    	    	
	    	    	tbar: [
	    	    	    'Filter By Genre:',
    	    	       	new App.vote4music.ComboBox({
    	    	       		width: 100,
	    	    	    	listeners: {
    	    	       			select: this._selectGenre,
    	    	       			scope: this
    	    	       		}
	    	    	    })  
	    	    	]
	    	    
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
		    var selNode = this.albumsView.getSelectedNodes();
		    var detailEl = this.previewPanel.body;
			if(selNode && selNode.length > 0){
				selNode = selNode[0];
			    var data = this.lookup[selNode.id];
	            detailEl.hide();
	            this.detailsTemplate.overwrite(detailEl, data);
	            detailEl.slideIn('l', {stopFx:true,duration:.2});
	            
	            this._attachVoteEvent(data.id);
			}else{
			    detailEl.update('');
			}
		},
		
		_attachVoteEvent: function(id){
			Ext.fly('voteLink').on('click', function(){
            	this.voteAlbum(id);
            }, this);
		},
		
		voteAlbum: function(id){
			Ext.Ajax.request({
				url: '/vote',
			   	success: function(response){
					var nbVotes = response.responseText * 1;
					var record = this.albumsView.store.getById(id);
					record.data.nbVotes = nbVotes

					this.lookup['album_' + id] = record.data;
					
					this.detailsTemplate.overwrite(this.previewPanel.body, record.data);
					this._attachVoteEvent(id);
				},
				params: { id: id+"" },
				scope: this
			});
		},
		
		_selectGenre: function(combo, record){
			var store = this.albumsView.store; 
			store.load({
				params: {genre: record.data.name},
				success: function(){
					this.albumsView.refresh();
				},
				scope: this
			});
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
    	renderTo: 'container'
    });
});
