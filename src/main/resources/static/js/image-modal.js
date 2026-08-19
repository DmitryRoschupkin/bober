document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('imageModal')
    const modalImg = document.getElementById('modalImage')
    const closeBtn = document.querySelector('.image-modal__close')
    const clickableImages = document.querySelectorAll('.js-pic-preview, .book-cover-img')

    clickableImages.forEach(img => {
        img.addEventListener('click', function () {
            if (modal && modalImg) {
                modal.style.display = 'block';
                modalImg.src = this.src;
            }
        });
    });

    function closeModal() {
        modal.style.display = 'none'
    }

    if(closeBtn) {
        closeBtn.addEventListener('click', closeModal)
    }

    if(modal) {
        modal.addEventListener('click', function (event) {
            if (event.target === modal) {
                closeModal()
            }
        })
    }

    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape' && modal.style.display === 'block') {
            closeModal()
        }
    })
})