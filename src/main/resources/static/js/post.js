document.addEventListener('click', e => {
    const editBtn = e.target.closest('.author-microblog__post-edit-btn');
    const commentBtn = e.target.closest('.comments-btn');

    if (editBtn) {
        const postBody = editBtn.closest('.author-microblog__post');
        if (postBody) {
            const editForm = postBody.querySelector(':scope > .author-microblog__post-edit-form');
            if (editForm) {
                const isOpen = !editForm.classList.contains('is-open');
                togglePostEditForm(editForm, isOpen);
            }
        }
    }

    if (commentBtn) {
        const postBody = commentBtn.closest('.author-microblog__post');
        if (postBody) {
            const commentsSection = postBody.querySelector('.post-comments-section')
                || (postBody.nextElementSibling?.classList.contains('post-comments-section')
                    ? postBody.nextElementSibling
                    : null);

            if (commentsSection) {
                const isOpen = !commentsSection.classList.contains('is-open');
                toggleCommentsSection(commentsSection, isOpen);
            }
        }
    }
});

function togglePostEditForm(editForm, show) {
    if (show) {
        editForm.classList.add('is-open');
        const input = editForm.querySelector('.author-microblog__post-edit-text');
        if (input) {
            input.focus();
            input.selectionStart = input.selectionEnd = input.value.length;
        }
    } else {
        editForm.classList.remove('is-open');
    }
}

function toggleCommentsSection(commentsSection, show) {
    if (show) {
        commentsSection.classList.add('is-open');
    } else {
        commentsSection.classList.remove('is-open');
    }
}