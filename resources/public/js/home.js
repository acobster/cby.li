var slugField = document.querySelector('[name=slug]');

function debounce(func, wait, immediate) {
	var timeout;
	return function() {
		var context = this, args = arguments;
		var later = function() {
			timeout = null;
			if (!immediate) func.apply(context, args);
		};
		var callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);
		if (callNow) func.apply(context, args);
	};
}

var slugError = document.querySelector('[data-js=slug-error]');
var submitBtn = document.querySelector('[data-js=submit]');

// Keep track of which slug we're checking. This will be important if there
// are multiple concurrent requests with network latency, since one can come
// back and clobber a more recently sent one.
var checkingSlug = null;

var checkSlug = debounce(function(e) {
  var field = e.target;
  var slug = field.value;
  if (slug) {
    checkingSlug = slug;
    fetch('/?slug=' + slug)
      .then(function(res) {
        if (res.url.indexOf('slug='+checkingSlug) > -1) {
          if (res.status === 200) {
            // Slug is good to go!
            field.classList.remove('error');
            slugError.classList.add('hidden');
            submitBtn.disabled = false;
          } else {
            // Slug exists in the db already.
            field.classList.add('error');
            slugError.innerHTML = 'a link for <code>/' + checkingSlug
              + '</code> already exists!';
            slugError.classList.remove('hidden');
            submitBtn.disabled = true;
          }
        }
      });
  } else {
    checkingSlug = null;
    field.classList.remove('error');
    slugError.classList.add('hidden');
    submitBtn.disabled = true;
  }
}, 300);

slugField.addEventListener('keyup', checkSlug);
