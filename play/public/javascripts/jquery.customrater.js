//copyright 2008 Jarrett Vance
//http://jvance.com
$.fn.rater = function(options) {
    var opts = $.extend({}, $.fn.rater.defaults, options);
    return this.each(function() {
        var $this = $(this);
        var $on = $this.find('.ui-rater-starsOn');
        var $off = $this.find('.ui-rater-starsOff');
        opts.size = $on.height();
        if (opts.rating == undefined) opts.rating = $on.width() / opts.size;
        if (opts.id == undefined) opts.id = $this.attr('id');
        $off.mousemove(function(e) {
            var left = e.clientX - $off.offset().left;
            var width = $off.width() - ($off.width() - left);
            width = Math.ceil(width / (opts.size / opts.step)) * opts.size / opts.step;
            $on.width(width);
        }).hover(function(e) {$on.addClass('ui-rater-starsHover');}, function(e) {
            $on.removeClass('ui-rater-starsHover');$on.width(opts.rating * opts.size);
        }).click(function(e) {
            var r = Math.round($on.width() / $off.width() * (opts.units * opts.step)) / opts.step;
            $off.unbind('click').unbind('mousemove').unbind('mouseenter').unbind('mouseleave');
            $off.css('cursor', 'default');$on.css('cursor', 'default');
            $.fn.rater.rate($this, opts, r);
        }).css('cursor', 'pointer');$on.css('cursor', 'pointer');
    });
};

$.fn.rater.defaults = {
    postHref: location.href,
    units: 5,
    step: 2
};

$.fn.rater.rate = function($this, opts, rating) {
    var $on = $this.find('.ui-rater-starsOn');
    var $off = $this.find('.ui-rater-starsOff');
    //custom : find album
    var $albumId = $this.attr('id');
    $off.fadeTo(600, 0.4, function() {
        $.ajax({
            url: opts.postHref,
            type: "POST",
            data: 'id=' + $albumId + '&rating=' + rating,
            complete: function(req) {
                if (req.status == 200) { //success
                    opts.rating = parseFloat(req.responseText);
                    $off.fadeTo(600, 0.1, function() {
                        $on.removeClass('ui-rater-starsHover').width(opts.rating * opts.size);
                        //custom : find table
                        $table=$('#albumList');
                        var $count = $table.find('.ui-rater-rateCount'+$albumId);
                        //custom : nb votes
                        var $nbVotes = parseInt($count.text());
                        $count.text($nbVotes + 1 + ' votes');
                        //custom : average rating, total
                        var $average = $table.find('.ui-rater-average'+$albumId);
                        $total = $table.find('.ui-rater-total'+$albumId);
                        $newTotal = parseFloat($total.text())+opts.rating;
                        $total.text($newTotal);
                        var $newAverage = $newTotal/($nbVotes+1);
                        $average.text($newAverage.toFixed(1));
                        $off.fadeTo(600, 1);
                        $this.attr('title', 'Your rating: ' + rating.toFixed(1));
                        //TODO cacher le rater
                    });
                } else { //failure
                    alert(req.responseText);
                    $on.removeClass('ui-rater-starsHover').width(opts.rating * opts.size);
                    $this.rater(opts);
                    $off.fadeTo(2200, 1);
                }
            }
        });
    });
};
