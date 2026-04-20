(function() {
    'use strict';

    function initMediaGallery(container) {
        const slides = container.querySelectorAll('.hero-slide');
        const prevBtn = container.querySelector('.carousel-arrow.prev');
        const nextBtn = container.querySelector('.carousel-arrow.next');
        const thumbnails = container.querySelectorAll('.thumbnail');
        const lightbox = container.querySelector('.lightbox');
        const lightboxVideoEl = container.querySelector('.lightbox-video');
        const closeLightbox = container.querySelector('.close-lightbox');
        let currentIndex = 0;

        // Show a specific slide by index
        function showSlide(index) {
            if (!slides.length) {
                return;
            }
            if (index < 0) {
                index = slides.length - 1;
            }
            if (index >= slides.length) {
                index = 0;
            }

            slides.forEach((slide, i) => {
                slide.classList.toggle('active', i === index);
                slide.setAttribute('aria-hidden', i === index ? 'false' : 'true');
            });
            thumbnails.forEach((thumb, i) => {
                thumb.classList.toggle('active', i === index);
                thumb.setAttribute('aria-selected', i === index ? 'true' : 'false');
            });
            currentIndex = index;

            // Scroll active thumbnail into view horizontally
            const activeThumb = thumbnails[index];
            if (activeThumb) {
                activeThumb.scrollIntoView({
                    behavior: 'smooth',
                    block: 'nearest',
                    inline: 'center'
                });
            }
        }

        function nextSlide() {
            showSlide(currentIndex + 1);
        }

        function prevSlide() {
            showSlide(currentIndex - 1);
        }

        if (prevBtn) {
            prevBtn.addEventListener('click', prevSlide);
        }
        if (nextBtn) {
            nextBtn.addEventListener('click', nextSlide);
        }

        // Keyboard arrow navigation on the hero carousel
        container.addEventListener('keydown', (e) => {
            if (e.key === 'ArrowRight') {
                e.preventDefault();
                nextSlide();
            }
            if (e.key === 'ArrowLeft') {
                e.preventDefault();
                prevSlide();
            }
        });

        // Thumbnail click + keyboard activation
        thumbnails.forEach((thumb, idx) => {
            thumb.addEventListener('click', () => showSlide(idx));
            thumb.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    showSlide(idx);
                }
                // Allow arrow keys to move focus between thumbnails
                if (e.key === 'ArrowRight' && thumbnails[idx + 1]) {
                    thumbnails[idx + 1].focus();
                }
                if (e.key === 'ArrowLeft' && thumbnails[idx - 1]) {
                    thumbnails[idx - 1].focus();
                }
            });
        });

        // Activate first slide if none active
        if (slides.length && !container.querySelector('.hero-slide.active')) {
            showSlide(0);
        }

        function closeLightboxFn() {
            if (!lightbox || !lightboxVideoEl) {
                return;
            }
            lightbox.style.display = 'none';
            lightboxVideoEl.pause();
            const sourceEl = lightboxVideoEl.querySelector('source');
            if (sourceEl) {
                sourceEl.src = '';
            }
            lightboxVideoEl.load();
        }

        if (lightbox && lightboxVideoEl && closeLightbox) {
            container.querySelectorAll('.play-video-btn').forEach(btn => {
                btn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    const videoUrl = btn.getAttribute('data-video-url');
                    const sourceEl = lightboxVideoEl.querySelector('source');
                    if (videoUrl) {
                        if (sourceEl) {
                            sourceEl.src = videoUrl;
                        }
                        lightboxVideoEl.load();
                        lightbox.style.display = 'flex';
                        lightboxVideoEl.play();
                    }
                });
            });

            closeLightbox.addEventListener('click', closeLightboxFn);

            // Close on backdrop click
            lightbox.addEventListener('click', (e) => {
                if (e.target === lightbox) {
                    closeLightboxFn();
                }
            });

            // Close on Escape key
            document.addEventListener('keydown', (e) => {
                if (e.key === 'Escape' && lightbox.style.display === 'flex') {
                    closeLightboxFn();
                }
            });
        }
    }

    // Initialize all media gallery banners on the page
    document.querySelectorAll('.media-gallery-banner').forEach(initMediaGallery);
})();
