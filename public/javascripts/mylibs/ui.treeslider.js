/**
*
* jQuery UI Widget of really neat GitHub TreeSlider.
*
* Use CSS3 Transitions and HTML5 History API. Thus, browsers that don't support css transitions or html5 history yet are not supported (should be?)
*
* @namespace ui
* @class TreeSlider
* @author mdaniel
*/
 (function($, W, D, H, L, undefined) {

   var historyAble = typeof Modernizr !== 'undefined' ? Modernizr.history : typeof H.pushState === 'function';

  $.widget('ui.treeslider', {
    options: {
      frames: {
        frame: 'frame',
        frames: 'frames',
        right: 'frame-right',
        left: 'frame-left',
        center: 'frame-center',
        loading: 'frame-loading'
      }
    },

    sliding: false,

    _create: function(o) {
      var self = this,
      o = this.options,
      slider = this.element,
      loading = this.loading = this.element.find('.' + o.frames.loading);

      if (!historyAble) {
        return;
      }

      H.replaceState({
        path: this.pathFromURL(L.pathname)
      }, '');

      slider
        .delegate('a, .breadcrumb a', 'click', function clickHandler(e) {
          if (e.which == 2 || e.metaKey || e.ctrlKey) {
            return true;
          } else {
            self._clickHandler(e);
            return false;
          }
        }).delegate('.' + o.frames.frame, 'webkitTransitionEnd transitionend', function transitionEnd(e) {
          if (self.sliding) {
            self.sliding = false;
            slider.find('.' + o.frames.right).hide();
            self.correctFrameHeight();
          }
        });
      
      this.element.find('.' + o.frames.center).attr('data-path', self.pathFromURL(L.pathname));

      $(W).bind('popstate', function popStateHandler(ev) {
        var e = ev.originalEvent;
        e.state && self.slideTo(L.pathname);
      });
    },
      
    _clickHandler: function(e) {
      var url, path;
      if (this.sliding) return false;
          
      url = $(e.currentTarget).attr('href');
      path = this.pathFromURL(url);
          
      H.pushState({
        path: path
      }, '', url);
          
      this.slideTo(url);
    },

    frameForPath: function(path) {
      return $('.frame[data-path="' + path + '"]')
    },

    frameForURL: function(path) {
      return this.frameForPath(this.pathFromURL(path));
    },
      
    pathFromURL: function(path) {
      return path === '/' ? '' : path;
    },
      
    slideTo: function(url) {
      var o = this.options,
      path = this.pathFromURL(url),
      frame = this.frameForPath(path),
      current = $('.' + o.frames.center).attr('data-path') || '';
      
      console.log(path, current, path.split('/').length, current.split('/').length);
      
      if(!frame.is('.' + o.frames.center)) {
        (path.split('/').length > current.split('/').length) ? this.slideForwardTo(url) : this.slideBackTo(url);
      }
    },

    slideForwardTo: function(url) {
      console.log('slideForwardTo');
      var self = this,
      o = this.options,
      frame, loading;
          
      if(this.sliding) {
        // return element, dont wan't to break the chain.
        return self.element;
      }
      
      this.sliding = true;
          
      frame = this.frameForURL(url);
          
      self.element.find('.' + o.frames.center).addClass(o.frames.left).removeClass(o.frames.center);
                      
      if (!frame.length) {
        loading = self.element.find('.'+ o.frames.loading).clone();
        self.element.find('.' + o.frames.left + ':last').after(loading);
        this.makeCenterFrame(loading);
        this.loadFrame(url, function(resp) {
          loading.replaceWith($(resp).find('.' + o.frames.center).attr('data-path', self.pathFromURL(url)));
        });
      } else {
        this.makeCenterFrame(frame);
      }
    },
      
    slideBackTo: function(url) {
        console.log('slideBackTo');
      var self = this, 
      o = this.options, 
      path = "", center, frame, loading;
          
      if(this.sliding) {
        // return element, dont wan't to break the chain.
        return self.element;
      }

      console.log('Sliding back to ', url);
      this.sliding = true;
          
      center = this.element.find('.' + o.frames.center);
      frame = this.frameForURL(url);
      path = this.pathFromURL(url);
          
      if (!frame.length) {
        loading = $('.frame-loading').clone();
        self.element.find('.' + o.frames.frames)
          .prepend(loading.show().addClass(o.frames.left));
                  
        setTimeout(function() {
          center.addClass(o.frames.right).removeClass(o.frames.center)
          self.makeCenterFrame(loading);
          self.loadFrame(url, function(resp) {
            loading.empty()
              .append($(resp).find('.' + o.frames.center).contents())
              .removeClass(o.frames.loading)
              .attr("data-path", path);
          })
        }, 50)
      } else {
        center.addClass(o.frames.right).removeClass(o.frames.center);
        this.makeCenterFrame(frame);
      }
    },
    
    makeCenterFrame: function(frame) {
      var o = this.options,
      self = this,
      bread = self.element.find(".breadcrumb[data-path='" + frame.attr("data-path") + "']");
      
      frame.show()
        .removeClass(o.frames.left + ' ' + o.frames.right)                
        .addClass(o.frames.center);
      
      frame
        .nextAll('.' + o.frames.left).hide()
        .removeClass(o.frames.left)
        .addClass(o.frames.right).show();
              
      frame
        .prevAll('.' + o.frames.right).hide()
        .removeClass(o.frames.right)
        .addClass(o.frames.left).show();
        
      if (bread.length) {
        self.element.find(".breadcrumb:visible").hide();
        bread.show();
      }
    },
    
    correctFrameHeight: function(){
      //this.element.height(this.element.find('.' + this.options.frames.center).height());
    },
      
    loadFrame: function(url, cb) {
      var self = this,
      o = this.options,
      center = this.element.find('.' + o.frames.center);
      
      $.ajax({
        url: url,
        success: function(resp) {
          cb.apply(this, arguments);      
          self.element.find('.breadcrumb').hide().last().after($(resp).find(".breadcrumb"));
          
          self.correctFrameHeight();
        },
        error: function() {
          center.html('<h3>Something went wrong.</h3>');
        }
      });
    }
  });
  
})(this.jQuery, this, this.document, this.history, this.location);